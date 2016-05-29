package io.irontest.models;

/**
 * Created by Zheng on 28/05/2016.
 */
public class IIBTeststepProperties extends Properties {
    public static final String ACTION_TYPE_START = "Start";
    public static final String ACTION_TYPE_STOP = "Stop";
    private String integrationServerName;
    private String messageFlowName;
    private String action;

    public String getIntegrationServerName() {
        return integrationServerName;
    }

    public void setIntegrationServerName(String integrationServerName) {
        this.integrationServerName = integrationServerName;
    }

    public String getMessageFlowName() {
        return messageFlowName;
    }

    public void setMessageFlowName(String messageFlowName) {
        this.messageFlowName = messageFlowName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
