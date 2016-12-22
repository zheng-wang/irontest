package io.irontest.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhenw9 on 22/12/2016.
 */
public class TeststepWrapper {
    private Teststep teststep;

    //  currently only used for conveying information to DB test step UI
    private Map<String, Object> parameters = new HashMap<String, Object>();

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
