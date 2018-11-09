package io.irontest.models;

public class HTTPStubMapping {
    private String specJson;

    public HTTPStubMapping() {}

    public HTTPStubMapping(String specJson) {
        this.specJson = specJson;
    }

    public String getSpecJson() {
        return specJson;
    }

    public void setSpecJson(String specJson) {
        this.specJson = specJson;
    }
}
