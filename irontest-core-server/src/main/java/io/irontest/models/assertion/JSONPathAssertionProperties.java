package io.irontest.models.assertion;

import io.irontest.models.Properties;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionProperties extends Properties {
    private String jsonPath;
    private String expectedValueJSON;

    public JSONPathAssertionProperties() {}

    public JSONPathAssertionProperties(String jsonPath, String expectedValueJSON) {
        this.jsonPath = jsonPath;
        this.expectedValueJSON = expectedValueJSON;
    }

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
