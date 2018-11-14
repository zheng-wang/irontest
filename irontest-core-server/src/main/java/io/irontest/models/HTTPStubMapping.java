package io.irontest.models;

public class HTTPStubMapping {
    private short number;
    private String specJson;

    public HTTPStubMapping() {}

    public HTTPStubMapping(short number, String specJson) {
        this.number = number;
        this.specJson = specJson;
    }

    public short getNumber() {
        return number;
    }

    public void setNumber(short number) {
        this.number = number;
    }

    public String getSpecJson() {
        return specJson;
    }

    public void setSpecJson(String specJson) {
        this.specJson = specJson;
    }
}
