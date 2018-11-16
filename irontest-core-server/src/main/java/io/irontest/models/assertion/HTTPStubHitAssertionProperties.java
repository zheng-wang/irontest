package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class HTTPStubHitAssertionProperties extends Properties {
    private short stubNumber;

    public HTTPStubHitAssertionProperties() {}

    public HTTPStubHitAssertionProperties(short stubNumber) {
        this.stubNumber = stubNumber;
    }

    public short getStubNumber() {
        return stubNumber;
    }

    public void setStubNumber(short stubNumber) {
        this.stubNumber = stubNumber;
    }
}
