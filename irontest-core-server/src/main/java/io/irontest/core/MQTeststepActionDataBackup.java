package io.irontest.core;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;
import io.irontest.models.teststep.MQRFH2Header;

import java.util.List;

/**
 * Only for MQ test step.
 * Created by Zheng on 25/06/2016.
 */
public class MQTeststepActionDataBackup {
    private IntegerEqualAssertionProperties queueDepthAssertionProperties; // only for CheckDepth action
    private List<Assertion> dequeueAssertions;    // only for Dequeue action

    //  for Enqueue and Publish actions
    private String textRequest;
    private byte[] fileRequest;
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

    public String getTextRequest() {
        return textRequest;
    }

    public void setTextRequest(String textRequest) {
        this.textRequest = textRequest;
    }

    public byte[] getFileRequest() {
        return fileRequest;
    }

    public void setFileRequest(byte[] fileRequest) {
        this.fileRequest = fileRequest;
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
