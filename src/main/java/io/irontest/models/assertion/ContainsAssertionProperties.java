package io.irontest.models.assertion;

import io.irontest.models.Properties;

/**
 * Created by Zheng on 19/07/2015.
 */
public class ContainsAssertionProperties extends Properties {
    private String contains;

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }
}
