package io.irontest.core;

import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionProperties;

/**
 * Created by Zheng on 25/06/2016.
 */
public class TeststepActionDataBackup {
    private IntegerEqualAssertionProperties queueDepthAssertionProperties; // only for MQ test step CheckDepth action
    private XMLEqualAssertionProperties dequeueAssertionProperties;    // only for MQ test step Dequeue action
    private Object enqueueMessage;      // only for MQ test step Enqueue action

    public IntegerEqualAssertionProperties getQueueDepthAssertionProperties() {
        return queueDepthAssertionProperties;
    }

    public void setQueueDepthAssertionProperties(IntegerEqualAssertionProperties queueDepthAssertionProperties) {
        this.queueDepthAssertionProperties = queueDepthAssertionProperties;
    }

    public XMLEqualAssertionProperties getDequeueAssertionProperties() {
        return dequeueAssertionProperties;
    }

    public void setDequeueAssertionProperties(XMLEqualAssertionProperties dequeueAssertionProperties) {
        this.dequeueAssertionProperties = dequeueAssertionProperties;
    }

    public Object getEnqueueMessage() {
        return enqueueMessage;
    }

    public void setEnqueueMessage(Object enqueueMessage) {
        this.enqueueMessage = enqueueMessage;
    }
}
