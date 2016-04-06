package io.irontest.models;

/**
 * Created by Zheng on 18/07/2015.
 */
public class TeststepInvocationResponse {
    private String response;


    public TeststepInvocationResponse() {}

    public TeststepInvocationResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
