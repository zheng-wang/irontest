package io.irontest.core.runner;

/**
 * Created by Zheng on 6/07/2016.
 */
public class SOAPTeststepRunResult {
    private String httpResponseContentType;  //  an HTTP header
    private String httpResponseBody;

    public String getHttpResponseContentType() {
        return httpResponseContentType;
    }

    public void setHttpResponseContentType(String httpResponseContentType) {
        this.httpResponseContentType = httpResponseContentType;
    }

    public String getHttpResponseBody() {
        return httpResponseBody;
    }

    public void setHttpResponseBody(String httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP response content type: ").append(httpResponseContentType).append(". HTTP response body: ")
                .append(httpResponseBody);
        return sb.toString();
    }
}
