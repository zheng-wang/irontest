package io.irontest.models.teststep;

import io.irontest.models.Properties;

public class WaitTeststepProperties extends Properties {
    private long milliseconds;

    public WaitTeststepProperties() {}

    public WaitTeststepProperties(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
