package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class XMLEqualAssertionProperties extends Properties {
    private String expectedXML;

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
