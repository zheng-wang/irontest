package io.irontest.models.teststep;

import io.irontest.models.HTTPMethod;
import io.irontest.models.Properties;

import java.util.ArrayList;
import java.util.List;

public class HTTPTeststepProperties extends Properties {
    private HTTPMethod httpMethod;

    //  using List instead of Map here to ease the display on ui-grid
    private List<HTTPHeader> httpHeaders = new ArrayList<HTTPHeader>();

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HTTPMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<HTTPHeader> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<HTTPHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
