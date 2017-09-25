package io.irontest.models.teststep;

import io.irontest.models.Properties;

/**
 * Created by zhenw9 on 10/06/2016.
 */
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
