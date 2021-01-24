package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonTypeName(EndpointProperties.FTP_ENDPOINT_PROPERTIES)
public class FTPEndpointProperties extends EndpointProperties {
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
        String protocol = isUseSSL() ? "ftpes" : "ftp";
        return protocol + "://" + host + ":" + port;
    }
}