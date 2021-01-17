package io.irontest.models.endpoint;

import io.irontest.models.Properties;

public class JMSEndpointProperties extends Properties {
    private String jmsProvider;

    public String getJmsProvider() {
        return jmsProvider;
    }

    public void setJmsProvider(String jmsProvider) {
        this.jmsProvider = jmsProvider;
    }
}
