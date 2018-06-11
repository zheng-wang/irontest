package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class JSONPathXMLEqualAssertionProperties extends Properties {
    private String jsonPath;
    private String expectedXML;

    public JSONPathXMLEqualAssertionProperties() {}

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
