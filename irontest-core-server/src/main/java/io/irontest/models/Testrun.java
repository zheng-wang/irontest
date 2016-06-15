package io.irontest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 7/24/15.
 */
public class Testrun {
    //  for test step run
    private Teststep teststep;
    private Object response;

    //  for test case run
    private Long testcaseId;
    private List<Long> failedTeststepIds = new ArrayList<Long>();

    public Testrun() {}

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(Long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public List<Long> getFailedTeststepIds() {
        return failedTeststepIds;
    }

    public void setFailedTeststepIds(List<Long> failedTeststepIds) {
        this.failedTeststepIds = failedTeststepIds;
    }

    public Teststep getTeststep() {
        return teststep;
    }

    public void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }
}
