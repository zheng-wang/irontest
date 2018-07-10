package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class StatusCodeEqualAssertionProperties extends Properties {
    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
