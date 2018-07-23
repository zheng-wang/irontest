package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView(ResourceJsonViews.TestcaseExport.class)
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
