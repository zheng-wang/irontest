package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class ContainsAssertionProperties extends Properties {
    private String contains;

    public ContainsAssertionProperties() {}

    public ContainsAssertionProperties(String contains) {
        this.contains = contains;
    }

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }
}
