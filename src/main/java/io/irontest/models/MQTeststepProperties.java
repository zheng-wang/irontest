package io.irontest.models;

import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionProperties;

/**
 * Created by Zheng on 30/05/2016.
 */
public class MQTeststepProperties extends Properties {
    public static final String ACTION_TYPE_CLEAR = "Clear";
    public static final String ACTION_TYPE_CHECK_DEPTH = "CheckDepth";
    public static final String ACTION_TYPE_DEQUEUE = "Dequeue";

    private String queueName;
    private String action;
    private IntegerEqualAssertionProperties queueDepthAssertionPropertiesBackup;
    private XMLEqualAssertionProperties dequeueAssertionPropertiesBackup;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
}
