package io.irontest.models;

import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionProperties;

/**
 * Created by Zheng on 30/05/2016.
 */
public class MQTeststepProperties extends Properties {
    public static final String ACTION_CLEAR = "Clear";
    public static final String ACTION_CHECK_DEPTH = "CheckDepth";
    public static final String ACTION_DEQUEUE = "Dequeue";
    public static final String ACTION_ENQUEUE = "Enqueue";
    public static final String ENQUEUE_MESSAGE_TYPE_TEXT = "Text";
    public static final String ENQUEUE_MESSAGE_TYPE_BINARY = "Binary";

    private String queueName;
    private String enqueueMessageType; // only for Enqueue action
    private String enqueueMessageFilename; // only for Enqueue action with message type Binary

    //  fields for backup
    private IntegerEqualAssertionProperties queueDepthAssertionPropertiesBackup;    //  only for CheckDepth action
    private XMLEqualAssertionProperties dequeueAssertionPropertiesBackup;    //  only for Dequeue action

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public IntegerEqualAssertionProperties getQueueDepthAssertionPropertiesBackup() {
        return queueDepthAssertionPropertiesBackup;
    }

    public void setQueueDepthAssertionPropertiesBackup(IntegerEqualAssertionProperties queueDepthAssertionPropertiesBackup) {
        this.queueDepthAssertionPropertiesBackup = queueDepthAssertionPropertiesBackup;
    }

    public XMLEqualAssertionProperties getDequeueAssertionPropertiesBackup() {
        return dequeueAssertionPropertiesBackup;
    }

    public void setDequeueAssertionPropertiesBackup(XMLEqualAssertionProperties dequeueAssertionPropertiesBackup) {
        this.dequeueAssertionPropertiesBackup = dequeueAssertionPropertiesBackup;
    }

    public String getEnqueueMessageType() {
        return enqueueMessageType;
    }

    public void setEnqueueMessageType(String enqueueMessageType) {
        this.enqueueMessageType = enqueueMessageType;
    }

    public String getEnqueueMessageFilename() {
        return enqueueMessageFilename;
    }

    public void setEnqueueMessageFilename(String enqueueMessageFilename) {
        this.enqueueMessageFilename = enqueueMessageFilename;
    }
}