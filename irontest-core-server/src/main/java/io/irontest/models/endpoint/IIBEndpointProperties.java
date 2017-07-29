package io.irontest.models.endpoint;

import io.irontest.models.Properties;

/**
 * Created by Zheng on 29/07/2017.
 */
public class IIBEndpointProperties extends Properties {
    private String host;
    private Integer port;

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
}
