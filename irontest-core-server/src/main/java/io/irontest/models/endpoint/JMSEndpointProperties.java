package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

public class JMSEndpointProperties extends EndpointProperties {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String jmsProvider;

    public String getJmsProvider() {
        return jmsProvider;
    }

    public void setJmsProvider(String jmsProvider) {
        this.jmsProvider = jmsProvider;
    }
}
