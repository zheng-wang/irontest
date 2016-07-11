package io.irontest.models;

/**
 * Created by Zheng on 30/05/2016.
 */
public class MQTeststepProperties extends Properties {
    public static final String ENQUEUE_MESSAGE_FROM_TEXT = "Text";
    public static final String ENQUEUE_MESSAGE_FROM_FILE = "File";

    private String queueName;
    private String enqueueMessageFrom; // only for Enqueue action
    private MQRFH2Header enqueueMessageRFH2Header = new MQRFH2Header();  // only for Enqueue action with message from text
    private String enqueueMessageFilename; // only for Enqueue action with message from file

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getEnqueueMessageFrom() {
        return enqueueMessageFrom;
    }

    public void setEnqueueMessageFrom(String enqueueMessageFrom) {
        this.enqueueMessageFrom = enqueueMessageFrom;
    }

    public String getEnqueueMessageFilename() {
        return enqueueMessageFilename;
    }

    public void setEnqueueMessageFilename(String enqueueMessageFilename) {
        this.enqueueMessageFilename = enqueueMessageFilename;
    }

    public MQRFH2Header getEnqueueMessageRFH2Header() {
        return enqueueMessageRFH2Header;
    }

    public void setEnqueueMessageRFH2Header(MQRFH2Header enqueueMessageRFH2Header) {
        this.enqueueMessageRFH2Header = enqueueMessageRFH2Header;
    }
}