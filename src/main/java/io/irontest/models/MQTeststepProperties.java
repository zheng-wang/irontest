package io.irontest.models;

/**
 * Created by Zheng on 30/05/2016.
 */
public class MQTeststepProperties extends Properties {
    public static final String ACTION_TYPE_CLEAR = "Clear";
    public static final String ACTION_TYPE_CHECK_DEPTH = "CheckDepth";

    private String queueName;
    private String action;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
