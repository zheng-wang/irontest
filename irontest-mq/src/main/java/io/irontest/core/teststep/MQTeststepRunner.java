package io.irontest.core.teststep;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.headers.MQMD;
import com.ibm.mq.headers.*;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.MQConnectionMode;
import io.irontest.models.endpoint.MQEndpointProperties;
import io.irontest.models.teststep.*;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Hashtable;

public class MQTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQTeststepRunner.class);

    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        String action = teststep.getAction();
        if (teststep.getAction() == null) {
            throw new Exception("Action not specified.");
        }
        MQTeststepProperties teststepProperties = (MQTeststepProperties) teststep.getOtherProperties();
        if (teststepProperties.getDestinationType() == null) {
            throw new Exception("Destination type not specified.");
        }

        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();

        APIResponse response = null;
        Endpoint endpoint = teststep.getEndpoint();
        MQEndpointProperties endpointProperties = (MQEndpointProperties) endpoint.getOtherProperties();
        MQQueueManager queueManager = null;
        try {
            //  connect to queue manager
            if (endpointProperties.getConnectionMode() == MQConnectionMode.BINDINGS) {
                queueManager = new MQQueueManager(endpointProperties.getQueueManagerName());
            } else {
                Hashtable qmConnProperties = new Hashtable();
                qmConnProperties.put(CMQC.HOST_NAME_PROPERTY,  endpoint.getHost());
                qmConnProperties.put(CMQC.PORT_PROPERTY, endpoint.getPort());
                qmConnProperties.put(CMQC.CHANNEL_PROPERTY, endpointProperties.getSvrConnChannelName());
                queueManager = new MQQueueManager(endpointProperties.getQueueManagerName(), qmConnProperties);
            }

            if (MQDestinationType.QUEUE == teststepProperties.getDestinationType()) {
                response = doQueueAction(queueManager, teststepProperties.getQueueName(), action,
                        teststep.getRequest(), teststepProperties.getRfh2Header());
            } else if (MQDestinationType.TOPIC == teststepProperties.getDestinationType()) {
                doTopicAction(queueManager, teststepProperties.getTopicString(), action, teststep.getRequest(),
                        teststepProperties.getRfh2Header());
            }
        } finally {
            if (queueManager != null) {
                queueManager.disconnect();
            }
        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }

    private APIResponse doQueueAction(MQQueueManager queueManager, String queueName, String action, Object request,
                                 MQRFH2Header rfh2Header) throws Exception {
        APIResponse response = null;
        MQQueue queue = null;
        int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED;
        try {
            //  open queue
            if (Teststep.ACTION_CHECK_DEPTH.equals(action)) {
                openOptions += CMQC.MQOO_INQUIRE;
            } else if (Teststep.ACTION_ENQUEUE.equals(action)) {
                openOptions += CMQC.MQOO_OUTPUT;
            }
            try {
                queue = queueManager.accessQueue(queueName, openOptions, null, null, null);
            } catch (MQException mqEx) {
                if (mqEx.getCompCode() == CMQC.MQCC_FAILED && mqEx.getReason() == CMQC.MQRC_UNKNOWN_OBJECT_NAME) {
                    throw new Exception("Queue \"" + queueName + "\" not found.");
                } else {
                    throw mqEx;
                }
            }

            //  do the action
            if (Teststep.ACTION_CLEAR.equals(action)) {
                clearQueue(queue);
            } else if (Teststep.ACTION_CHECK_DEPTH.equals(action)) {
                response = new MQCheckQueueDepthResponse();
                ((MQCheckQueueDepthResponse) response).setQueueDepth(queue.getCurrentDepth());
            } else if (Teststep.ACTION_DEQUEUE.equals(action)) {
                response = dequeue(queue);
            } else if (Teststep.ACTION_ENQUEUE.equals(action)) {
                enqueue(queue, request, rfh2Header);
            } else {
                throw new Exception("Unrecognized action " + action + ".");
            }
        } finally {
            if (queue != null) {
                queue.close();
            }
        }

        return response;
    }

    private void doTopicAction(MQQueueManager queueManager, String topicString, String action, Object data,
                               MQRFH2Header rfh2Header) throws Exception {
        if ("".equals(StringUtils.trimToEmpty(topicString))) {
            throw new Exception("Topic string not specified.");
        }

        MQTopic publisher = null;
        try {
            //  open topic for publish
            publisher = queueManager.accessTopic(topicString,null, CMQC.MQTOPIC_OPEN_AS_PUBLICATION,
                    CMQC.MQOO_OUTPUT);

            if (Teststep.ACTION_PUBLISH.equals(action)) {
                MQMessage message = buildMessage(data, rfh2Header);
                publisher.put(message);
            } else {
                throw new Exception("Unrecognized action " + action + ".");
            }
        } finally {
            if (publisher != null) {
                publisher.close();
            }
        }
    }

    private MQMessage buildMessage(Object data, MQRFH2Header rfh2Header) throws Exception {
        if (data == null) {
            throw new Exception("Data can not be null.");
        }

        MQMessage message;
        if (data instanceof String) {
            message = buildMessageFromText((String) data, rfh2Header);
        } else {
            message = buildMessageFromFile((byte[]) data);
        }
        return message;
    }

    private void enqueue(MQQueue queue, Object data, MQRFH2Header rfh2Header) throws Exception {
        MQMessage message = buildMessage(data, rfh2Header);
        MQPutMessageOptions pmo = new MQPutMessageOptions();
        queue.put(message, pmo);
    }

    private MQMessage buildMessageFromText(String body, MQRFH2Header rfh2Header)
            throws IOException, MQDataException {
        MQMessage message = new MQMessage();

        //  create MQMD properties on the message object (MQMD is not written into message, but is used by MQ PUT)
        MQMD mqmd = new MQMD();
        message.putDateTime = new GregorianCalendar();
        mqmd.setEncoding(CMQC.MQENC_REVERSED);

        if (rfh2Header == null) {
            mqmd.copyTo(message);
        } else {   //  add RFH2 header if included
            mqmd.setFormat(CMQC.MQFMT_RF_HEADER_2);
            mqmd.setCodedCharSetId(CMQC.MQCCSI_DEFAULT);
            mqmd.setPersistence(CMQC.MQPER_PERSISTENT);
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
        MQMD mqmdHeader;
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

    private MQDequeueResponse dequeue(MQQueue queue) throws MQException, IOException, MQDataException {
        MQDequeueResponse result = null;
        MQGetMessageOptions getOptions = new MQGetMessageOptions();
        //  The MQGMO_PROPERTIES_FORCE_MQRFH2 is to enforce message properties to be returned in the MQRFH2 headers.
        //  This is so that user can see message properties with names reserved by MQRFH2 folders (like <mqps> or <usr>) as MQRFH2 folders.
        getOptions.options = CMQC.MQGMO_NO_WAIT + CMQC.MQGMO_FAIL_IF_QUIESCING + CMQC.MQGMO_PROPERTIES_FORCE_MQRFH2;
        MQMessage message = new MQMessage();
        try {
            queue.get(message, getOptions);

            //  create the response object only when there is message returned from the queue
            //  when there is no message on the queue, keep the response object as null (i.e. there is no response)
            result = new MQDequeueResponse();

            //  parse the MQMessage to Iron Test model
            MQRFH2Header mqrfh2Header = null;
            MQHeaderIterator it = new MQHeaderIterator(message);
            while (it.hasNext()) {
                MQHeader header = it.nextHeader();
                if (header instanceof MQRFH2) {
                    mqrfh2Header = new MQRFH2Header();
                    MQRFH2 mqrfh2 = (MQRFH2) header;
                    String[] folderStrings = mqrfh2.getFolderStrings();
                    for (int i = 0; i < folderStrings.length; i++) {
                        MQRFH2Folder mqrfh2Folder = new MQRFH2Folder();
                        mqrfh2Folder.setString(folderStrings[i]);
                        IronTestUtils.validateMQRFH2FolderStringAndSetFolderName(mqrfh2Folder);
                        mqrfh2Header.getFolders().add(mqrfh2Folder);
                    }
                }
            }
            result.setMqrfh2Header(mqrfh2Header);
            result.setBodyAsText(it.getBodyAsText());
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