package io.irontest.core.runner;

/**
 * Used for standalone test step running.
 * Created by Zheng on 15/01/2017.
 */
public class BasicTeststepRun {
    private Object response;            //  API response (could be null when such as no endpoint)
    private String infoMessage;         //  some additional information when the test step finishes running successfully

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }
}
