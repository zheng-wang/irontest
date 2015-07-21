package au.com.billon.stt.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by Zheng on 12/07/2015.
 */
@JsonDeserialize(using=TeststepInvocationRequestDeserializer.class)
public class TeststepInvocationRequest {
    public static final String TESTSTEP_INVOCATION_TYPE_SOAP = "SOAP";
    private String type;
    private String request;
    private Properties properties;

    public TeststepInvocationRequest(String type, String request, Properties properties) {
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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
