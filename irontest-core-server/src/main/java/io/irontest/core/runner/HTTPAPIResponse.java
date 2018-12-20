package io.irontest.core.runner;

import io.irontest.models.teststep.HTTPHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for both SOAP API and generic HTTP API.
 */
public class HTTPAPIResponse extends APIResponse {
    private int statusCode;
    private List<HTTPHeader> httpHeaders = new ArrayList<HTTPHeader>();
    private String httpBody;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

    public List<HTTPHeader> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<HTTPHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
