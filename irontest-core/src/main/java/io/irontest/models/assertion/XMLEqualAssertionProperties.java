package io.irontest.models.assertion;

import io.irontest.models.Properties;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionProperties extends Properties {
    private String expectedXML;

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
