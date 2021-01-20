package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

public class FTPEndpointProperties extends EndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String host;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private Integer port;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private boolean useSSL = false;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    /**
     * Used to unify FTP server URL display on test step action tab and test case run report.
     * @return
     */
    @JsonProperty
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    public String getFtpServerURL() {
        String protocol = isUseSSL() ? "ftpes" : "ftp";
        return protocol + "://" + host + ":" + port;
    }

    @JsonIgnore
    public void setFtpServerURL(String ftpServerURL) {
        //  do nothing
    }
}