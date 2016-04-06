package io.irontest.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by Zheng on 12/07/2015.
 */
public class TeststepInvocationRequest {
    public static final String TESTSTEP_INVOCATION_TYPE_SOAP = "SOAP";
    private String type;
    private String request;
    private TeststepInvocationRequestProperties properties;

    public TeststepInvocationRequest() {}

    public TeststepInvocationRequest(String type, String request, TeststepInvocationRequestProperties properties) {
        this.type = type;
        this.request = request;
        this.properties = properties;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TeststepInvocationRequestProperties getProperties() {
        return properties;
    }

    @JsonDeserialize(using=TeststepInvocationRequestPropertiesDeserializer.class)
    public void setProperties(TeststepInvocationRequestProperties properties) {
        this.properties = properties;
    }
}
