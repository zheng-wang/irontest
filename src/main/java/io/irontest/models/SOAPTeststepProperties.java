package io.irontest.models;

/**
 * Created by Zheng on 21/07/2015.
 */
public class SOAPTeststepProperties extends Properties {
    private String wsdlUrl;
    private String wsdlBindingName;
    private String wsdlOperationName;

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getWsdlBindingName() {
        return wsdlBindingName;
    }

    public void setWsdlBindingName(String wsdlBindingName) {
        this.wsdlBindingName = wsdlBindingName;
    }

    public String getWsdlOperationName() {
        return wsdlOperationName;
    }

    public void setWsdlOperationName(String wsdlOperationName) {
        this.wsdlOperationName = wsdlOperationName;
    }
}
