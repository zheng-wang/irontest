package io.irontest.models.assertion;

import io.irontest.models.Properties;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionProperties extends Properties {
    private String jsonPath;
    private String expectedValue;

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }
}
