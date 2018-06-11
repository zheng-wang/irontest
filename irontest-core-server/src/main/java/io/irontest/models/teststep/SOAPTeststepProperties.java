package io.irontest.models.teststep;

import io.irontest.models.Properties;

import java.util.ArrayList;
import java.util.List;

public class SOAPTeststepProperties extends Properties {
    //  using List instead of Map here to ease the display on ui-grid
    private List<HTTPHeader> httpHeaders = new ArrayList<HTTPHeader>();

    public List<HTTPHeader> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<HTTPHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
