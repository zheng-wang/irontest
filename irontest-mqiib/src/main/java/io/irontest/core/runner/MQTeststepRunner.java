package io.irontest.core.runner;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQHeaderIterator;
import com.ibm.mq.headers.MQMD;
import com.ibm.mq.headers.MQRFH2;
import io.irontest.db.TeststepDAO;
import io.irontest.models.MQIIBEndpointProperties;
import io.irontest.models.MQRFH2Header;
import io.irontest.models.MQTeststepProperties;
import io.irontest.models.Teststep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class MQTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTeststepRunner.class);
    //  disable the default 2033 logging (seems not needed since IBM MQ 8.0)
    static {
        MQException.logExclude(CMQC.MQRC_NO_MSG_AVAILABLE);
    }

    @Override
    protected void preRun(Teststep teststep, TeststepDAO teststepDAO) {
        //  fetch request for Enqueue action with message from file
        if (Teststep.ACTION_ENQUEUE.equals(teststep.getAction())) {
            MQTeststepProperties properties = (MQTeststepProperties) teststep.getOtherProperties();
            if (MQTeststepProperties.ENQUEUE_MESSAGE_FROM_FILE.equals(properties.getEnqueueMessageFrom())) {
                teststep.setRequest(teststepDAO.getBinaryRequestById(teststep.getId()));
            }
        }
    }

    public Object run(Teststep teststep) throws MQException, IOException, MQDataException {
        String action = teststep.getAction();
        if (action == null) {
            throw new RuntimeException("Action not specified.");
        }

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
                enqueue(queue, teststep.getRequest(), teststepProperties);
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

    private void enqueue(MQQueue queue, Object data, MQTeststepProperties teststepProperties) throws MQException, IOException, MQDataException {
        if (data == null) {
            throw new RuntimeException("Can not enqueue null.");
        }

        MQMessage message = null;
        if (data instanceof String) {
            message = buildMessageFromText((String) data, teststepProperties);
        } else {
            message = buildMessageFromFile((byte[]) data);
        }

        MQPutMessageOptions pmo = new MQPutMessageOptions();
        queue.put(message, pmo);
    }

    private MQMessage buildMessageFromText(String body, MQTeststepProperties teststepProperties)
            throws IOException, MQDataException {
        MQMessage message = new MQMessage();

        //  add RFH2 header if included
        MQRFH2Header rfh2Header = teststepProperties.getEnqueueMessageRFH2Header();
        if (rfh2Header.isEnabled()) {
            //  create MQMD properties on the message object (MQMD is not written into message, but is used by MQ PUT)
            MQMD mqmd = new MQMD();
            mqmd.setFormat(CMQC.MQFMT_RF_HEADER_2);
            mqmd.setEncoding(CMQC.MQENC_REVERSED);
            mqmd.setCodedCharSetId(CMQC.MQCCSI_DEFAULT);
            mqmd.setPersistence(CMQC.MQPER_PERSISTENT);
            message.putDateTime = new GregorianCalendar();
            mqmd.copyTo(message);

            //  populate RFH2 header
            MQRFH2 mqrfh2 = new MQRFH2();
            mqrfh2.setFolderStrings(rfh2Header.getFolderStrings());
            mqrfh2.write(message);
        }

        //  populate message body
        message.writeString(body);

        return message;
    }

    private MQMessage buildMessageFromFile(byte[] bytes) throws MQDataException, IOException {
        MQMessage message = new MQMessage();
        MQMD mqmdHeader = null;
        try {
            mqmdHeader = new MQMD(new DataInputStream(new ByteArrayInputStream(bytes)),
                    CMQC.MQENC_REVERSED, CMQC.MQCCSI_DEFAULT);
        } catch (Exception e) {
            LOGGER.info("Not able to construct MQMD out of the bytes. Exception details: ", e);
            mqmdHeader = null;
        }
        if (mqmdHeader != null && CMQC.MQMD_STRUC_ID.equals(mqmdHeader.getStrucId()) &&
                (CMQC.MQMD_VERSION_1 == mqmdHeader.getVersion() || CMQC.MQMD_VERSION_2 == mqmdHeader.getVersion())) {
            LOGGER.info("MQMD constructed. Writing other bytes as application data.");
            message.putDateTime = new GregorianCalendar();
            mqmdHeader.copyTo(message);
            message.persistence = CMQC.MQPER_PERSISTENT;
            message.write(bytes, MQMD.SIZE2, bytes.length - MQMD.SIZE2);
        } else {
            LOGGER.info("No valid MQMD. Writing all bytes as application data.");
            message.write(bytes);
        }
        return message;
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