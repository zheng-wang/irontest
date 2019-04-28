package io.irontest.models.teststep;

import io.irontest.models.Properties;

public class AMQPTeststepProperties extends Properties {
    private String nodeAddress;

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }
}
