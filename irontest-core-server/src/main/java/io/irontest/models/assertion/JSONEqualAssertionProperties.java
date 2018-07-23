package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView(ResourceJsonViews.TestcaseExport.class)
public class JSONEqualAssertionProperties extends Properties {
    private String expectedJSON;

    public String getExpectedJSON() {
        return expectedJSON;
    }

    public void setExpectedJSON(String expectedJSON) {
        this.expectedJSON = expectedJSON;
    }
}
