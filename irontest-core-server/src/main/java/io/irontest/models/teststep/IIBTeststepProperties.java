package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class IIBTeststepProperties extends Properties {
    private String integrationServerName;
    private String applicationName;
    private String messageFlowName;
    private Integer waitForProcessingCompletionTimeout = 20;        // in seconds

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

    public Integer getWaitForProcessingCompletionTimeout() {
        return waitForProcessingCompletionTimeout;
    }

    public void setWaitForProcessingCompletionTimeout(Integer waitForProcessingCompletionTimeout) {
        this.waitForProcessingCompletionTimeout = waitForProcessingCompletionTimeout;
    }
}
