package io.irontest.core.runner;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import io.irontest.models.MQIIBEndpointProperties;
import io.irontest.models.MQTeststepProperties;
import io.irontest.models.Teststep;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class MQTeststepRunner implements TeststepRunner {
    public Object run(Teststep teststep) throws MQException, IOException {
        Object result = null;
        MQIIBEndpointProperties endpointProperties = (MQIIBEndpointProperties) teststep.getEndpoint().getOtherProperties();
        MQTeststepProperties teststepProperties = (MQTeststepProperties) teststep.getOtherProperties();
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
            if (MQTeststepProperties.ACTION_TYPE_CHECK_DEPTH.equals(teststepProperties.getAction())) {
                openOptions += CMQC.MQOO_INQUIRE;
            }
            queue = queueManager.accessQueue(teststepProperties.getQueueName(), openOptions, null, null, null);

            if (MQTeststepProperties.ACTION_TYPE_CLEAR.equals(teststepProperties.getAction())) {
                MQGetMessageOptions getOptions = new MQGetMessageOptions();
                getOptions.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING;
                while (true) {
                    //  read message from queue
                    MQMessage message = new MQMessage();
                    try {
                        queue.get(message, getOptions);
                        System.out.println(message.readStringOfByteLength(message.getDataLength()));
                    } catch(MQException mqEx) {
                        if (mqEx.getCompCode() == CMQC.MQCC_FAILED && mqEx.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE) {
                            //  no more message left on the queue
                            System.out.println("No more message left on queue " + teststepProperties.getQueueName());
                            break;
                        } else {
                            throw mqEx;
                        }
                    }
                }
                result = true;
            } else if (MQTeststepProperties.ACTION_TYPE_CHECK_DEPTH.equals(teststepProperties.getAction())) {
                int queueDepth = queue.getCurrentDepth();
                System.out.println("Queue depth: " + queueDepth);
                result = queueDepth;
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
}