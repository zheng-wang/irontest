package au.com.billon.stt.models;

import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
public class WSDLBinding {
    private String name;
    private List<String> operations;

    public WSDLBinding() {}

    public WSDLBinding(String name) {
        this.name = name;
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
