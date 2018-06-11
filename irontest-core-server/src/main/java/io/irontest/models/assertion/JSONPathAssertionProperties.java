package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class JSONPathAssertionProperties extends Properties {
    private String jsonPath;
    private String expectedValueJSON;

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getExpectedValueJSON() {
        return expectedValueJSON;
    }

    public void setExpectedValueJSON(String expectedValueJSON) {
        this.expectedValueJSON = expectedValueJSON;
    }
}
