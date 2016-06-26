package io.irontest.core.runner;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQHeaderIterator;
import com.ibm.mq.headers.MQMD;
import io.irontest.models.MQIIBEndpointProperties;
import io.irontest.models.MQTeststepProperties;
import io.irontest.models.Teststep;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class MQTeststepRunner implements TeststepRunner {
    //  disable the default 2033 logging (seems not needed since IBM MQ 8.0)
    static {
        MQException.logExclude(CMQC.MQRC_NO_MSG_AVAILABLE);
    }

    public Object run(Teststep teststep) throws MQException, IOException, MQDataException {
        Object result = null;
        MQIIBEndpointProperties endpointProperties = (MQIIBEndpointProperties) teststep.getEndpoint().getOtherProperties();
        MQTeststepProperties teststepProperties = (MQTeststepProperties) teststep.getOtherProperties();
        String action = teststep.getAction();
        Hashtable qmConnProperties = new Hashtable();
        qmConnProperties.put(CMQC.HOST_NAME_PROPERTY,  endpointProperties.getHost());
        qmConnProperties.put(CMQC.PORT_PROPERTY, endpointProperties.getPort());
        qmConnProperties.put(CMQC.CHANNEL_PROPERTY, endpointProperties.getSvrConnChannelName());
        MQQueueManager queueManager = null;
        MQQueue queue = null;
        int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED;
        try {
            //  connect to queue manager
            queueManager = new MQQueueManager(endpointProperties.getQueueManagerName(), qmConnProperties);

            //  open queue
            if (Teststep.ACTION_CHECK_DEPTH.equals(action)) {
                openOptions += CMQC.MQOO_INQUIRE;
            } else if (Teststep.ACTION_ENQUEUE.equals(action)) {
                openOptions += CMQC.MQOO_OUTPUT;
            }
            queue = queueManager.accessQueue(teststepProperties.getQueueName(), openOptions, null, null, null);

            //  do the action
            if (Teststep.ACTION_CLEAR.equals(action)) {
                clearQueue(queue);
                result = true;
            } else if (Teststep.ACTION_CHECK_DEPTH.equals(action)) {
                result = queue.getCurrentDepth();
            } else if (Teststep.ACTION_DEQUEUE.equals(action)) {
                result = dequeue(queue);
            } else if (Teststep.ACTION_ENQUEUE.equals(action)) {
                enqueue(queue, teststep.getRequest());
                result = true;
            }
        } finally {
            if (queue != null) {
                queue.close();
            }
            if (queueManager != null) {
                queueManager.disconnect();
            }
        }

        return result;
    }

    private void enqueue(MQQueue queue, Object data) throws MQException, IOException, MQDataException {
        MQMessage message = new MQMessage();
        if (data instanceof String) {
            message.writeString((String) data);
        } else {
            byte[] bytes = (byte[]) data;
            MQMD mqmdHeader = new MQMD(new DataInputStream(new ByteArrayInputStream(bytes)),
                    CMQC.MQENC_REVERSED, CMQC.MQCCSI_DEFAULT);
            message.putDateTime = new GregorianCalendar();
            mqmdHeader.copyTo(message);
            message.persistence = CMQC.MQPER_PERSISTENT;
            message.write(bytes, mqmdHeader.size(), bytes.length - mqmdHeader.size());
        }
        MQPutMessageOptions pmo = new MQPutMessageOptions();
        queue.put(message, pmo);
    }

    private String dequeue(MQQueue queue) throws MQException, IOException, MQDataException {
        String result = null;
        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        getOptions.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING;
        MQMessage message = new MQMessage();
        try {
            queue.get(message, getOptions);
            MQHeaderIterator it = new MQHeaderIterator(message);
            result = it.getBodyAsText();
        } catch(MQException mqEx) {
            if (mqEx.getCompCode() == CMQC.MQCC_FAILED && mqEx.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE) {
                //  No more message available on the queue
            } else {
                throw mqEx;
            }
        }
        return result;
    }

    private void clearQueue(MQQueue queue) throws MQException {
        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        getOptions.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING;
        while (true) {
            //  read message from queue
            MQMessage message = new MQMessage();
            try {
                queue.get(message, getOptions);
            } catch(MQException mqEx) {
                if (mqEx.getCompCode() == CMQC.MQCC_FAILED && mqEx.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE) {
                    //  No more message available on the queue
                    break;
                } else {
                    throw mqEx;
                }
            }
        }
    }
}