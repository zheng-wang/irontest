package io.irontest.core.teststep;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class JMSBrowseQueueResponse extends APIResponse {
    private Map<String, String> header = new LinkedHashMap();
    private Map<String, String> properties = new TreeMap();
    private String body;

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
