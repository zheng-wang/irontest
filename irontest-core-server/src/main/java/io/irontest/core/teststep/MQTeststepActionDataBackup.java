package io.irontest.core.teststep;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.teststep.MQRFH2Header;

import java.util.List;

/**
 * Only for MQ test step.
 */
public class MQTeststepActionDataBackup {
    private IntegerEqualAssertionProperties queueDepthAssertionProperties; // only for CheckDepth action
    private List<Assertion> dequeueAssertions;    // only for Dequeue action

    //  for Enqueue and Publish actions
    private byte[] request;
    private String requestFilename;
    private MQRFH2Header rfh2Header;

    public IntegerEqualAssertionProperties getQueueDepthAssertionProperties() {
        return queueDepthAssertionProperties;
    }

    public void setQueueDepthAssertionProperties(IntegerEqualAssertionProperties queueDepthAssertionProperties) {
        this.queueDepthAssertionProperties = queueDepthAssertionProperties;
    }

    public List<Assertion> getDequeueAssertions() {
        return dequeueAssertions;
    }

    public void setDequeueAssertions(List<Assertion> dequeueAssertions) {
        this.dequeueAssertions = dequeueAssertions;
    }

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public String getRequestFilename() {
        return requestFilename;
    }

    public void setRequestFilename(String requestFilename) {
        this.requestFilename = requestFilename;
    }

    public MQRFH2Header getRfh2Header() {
        return rfh2Header;
    }

    public void setRfh2Header(MQRFH2Header rfh2Header) {
        this.rfh2Header = rfh2Header;
    }
}
