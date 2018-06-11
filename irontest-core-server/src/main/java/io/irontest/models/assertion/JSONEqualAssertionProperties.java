package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class JSONEqualAssertionProperties extends Properties {
    private String expectedJSON;

    public String getExpectedJSON() {
        return expectedJSON;
    }

    public void setExpectedJSON(String expectedJSON) {
        this.expectedJSON = expectedJSON;
    }
}
