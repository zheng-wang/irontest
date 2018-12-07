package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class HTTPStubHitAssertionProperties extends Properties {
    private short stubNumber;
    private short expectedHitCount;

    public HTTPStubHitAssertionProperties() {}

    public HTTPStubHitAssertionProperties(short stubNumber, short expectedHitCount) {
        this.stubNumber = stubNumber;
        this.expectedHitCount = expectedHitCount;
    }

    public short getStubNumber() {
        return stubNumber;
    }

    public void setStubNumber(short stubNumber) {
        this.stubNumber = stubNumber;
    }

    public short getExpectedHitCount() {
        return expectedHitCount;
    }

    public void setExpectedHitCount(short expectedHitCount) {
        this.expectedHitCount = expectedHitCount;
    }
}
