package io.irontest.core.teststep;

import java.util.LinkedHashMap;
import java.util.Map;

public class JMSBrowseQueueResponse extends APIResponse {
    private String body;
    private Map<String, String> header = new LinkedHashMap();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
