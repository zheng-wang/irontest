package io.irontest.models.endpoint;

public class JMSEndpointProperties extends EndpointProperties {
    private String jmsProvider;

    public String getJmsProvider() {
        return jmsProvider;
    }

    public void setJmsProvider(String jmsProvider) {
        this.jmsProvider = jmsProvider;
    }
}
