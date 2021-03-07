package io.irontest.core.teststep;

import io.irontest.models.teststep.HTTPHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for both SOAP API and generic HTTP API.
 */
public class HTTPAPIResponse extends APIResponse {
    private int statusCode;
    private List<HTTPHeader> httpHeaders = new ArrayList<>();
    private String httpBody;
    private long responseTime;     //  in milliseconds

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

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
