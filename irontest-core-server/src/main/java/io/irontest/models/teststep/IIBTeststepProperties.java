package io.irontest.models.teststep;

import io.irontest.models.Properties;

public class IIBTeststepProperties extends Properties {
    private String integrationServerName;
    private String applicationName;
    private String messageFlowName;

    public String getIntegrationServerName() {
        return integrationServerName;
    }

    public void setIntegrationServerName(String integrationServerName) {
        this.integrationServerName = integrationServerName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getMessageFlowName() {
        return messageFlowName;
    }

    public void setMessageFlowName(String messageFlowName) {
        this.messageFlowName = messageFlowName;
    }
}
