package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

import java.util.HashMap;
import java.util.Map;

@JsonView(ResourceJsonViews.TeststepEdit.class)
public class TeststepWrapper {
    private Teststep teststep;

    //  currently only used for conveying information to DB test step UI
    private Map<String, Object> parameters = new HashMap<>();

    public Teststep getTeststep() {
        return teststep;
    }

    public void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
