package io.irontest.models;

import java.util.List;

public class WSDLBinding {
    private String name;
    private List<String> operations;

    public WSDLBinding() {}

    public WSDLBinding(String name, List<String> operations) {
        this.name = name;
        this.operations = operations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }
}
