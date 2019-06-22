package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class IntegerEqualAssertionProperties extends Properties {
    private int number;

    public IntegerEqualAssertionProperties() {}

    public IntegerEqualAssertionProperties(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
