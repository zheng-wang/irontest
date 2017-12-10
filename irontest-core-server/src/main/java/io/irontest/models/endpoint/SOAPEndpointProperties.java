package io.irontest.models.endpoint;

import io.irontest.models.Properties;

/**
 * Created by Zheng on 13/06/2017.
 */
public class SOAPEndpointProperties extends Properties {
    private String wsdlURL = "?wsdl";
    private boolean wsdlURLByConvention = true;

    public String getWsdlURL() {
        return wsdlURL;
    }

    public void setWsdlURL(String wsdlURL) {
        this.wsdlURL = wsdlURL;
    }

    public boolean isWsdlURLByConvention() {
        return wsdlURLByConvention;
    }

    public void setWsdlURLByConvention(boolean wsdlURLByConvention) {
        this.wsdlURLByConvention = wsdlURLByConvention;
    }
}
