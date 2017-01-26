package io.irontest.models.teststep;

import io.irontest.models.Properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zheng on 26/01/2017.
 */
public class SOAPTeststepProperties extends Properties {
    private Map<String, String> httpHeaders = new HashMap<String, String>();

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
