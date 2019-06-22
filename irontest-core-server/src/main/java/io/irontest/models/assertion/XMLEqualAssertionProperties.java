package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class XMLEqualAssertionProperties extends Properties {
    private String expectedXML;

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
