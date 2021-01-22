package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.IIB_ENDPOINT_PROPERTIES)
public class IIBEndpointProperties extends EndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private boolean useSSL = false;

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    @Override
    public String constructUrl(String host, Integer port) {
        return (isUseSSL() ? "https" : "http") + "://" + host + ":" + port;
    }
}