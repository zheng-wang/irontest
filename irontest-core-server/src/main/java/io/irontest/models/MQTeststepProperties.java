package io.irontest.models;

/**
 * Created by Zheng on 30/05/2016.
 */
public class MQTeststepProperties extends Properties {
    public static final String ENQUEUE_MESSAGE_TYPE_TEXT = "Text";
    public static final String ENQUEUE_MESSAGE_TYPE_BINARY = "Binary";

    private String queueName;
    private String enqueueMessageType; // only for Enqueue action
    private String enqueueMessageFilename; // only for Enqueue action with message type Binary

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
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