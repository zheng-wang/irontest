package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.SOAP_ENDPOINT_PROPERTIES)
@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class SOAPEndpointProperties extends EndpointProperties {
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
