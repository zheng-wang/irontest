package io.irontest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 7/24/15.
 */
public class Testrun {
    //  for test step run
    private Long teststepId;
    private String request;
    private Object response;

    //  for test case run
    private Long testcaseId;
    private List<Long> failedTeststepIds = new ArrayList<Long>();

    public Testrun() {}

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

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

    public Long getTeststepId() {
        return teststepId;
    }

    public void setTeststepId(Long teststepId) {
        this.teststepId = teststepId;
    }

    public List<Long> getFailedTeststepIds() {
        return failedTeststepIds;
    }

    public void setFailedTeststepIds(List<Long> failedTeststepIds) {
        this.failedTeststepIds = failedTeststepIds;
    }
}
