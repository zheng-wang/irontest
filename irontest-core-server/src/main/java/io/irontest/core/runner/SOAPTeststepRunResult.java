package io.irontest.core.runner;

/**
 * Created by Zheng on 6/07/2016.
 */
public class SOAPTeststepRunResult {
    private String responseContentType;  //  an HTTP header
    private String responseBody;

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response content type: ").append(responseContentType).append(". Response body: ")
                .append(responseBody);
        return sb.toString();
    }
}
