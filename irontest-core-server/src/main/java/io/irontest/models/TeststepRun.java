package io.irontest.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 24/07/2016.
 */
public class TeststepRun {
    private Teststep teststep;
    private Date startTime;
    private long duration;              //  number of milliseconds
    private Object response;            //  endpoint response (could be unavailable when such as no endpoint); used for assertion verification against
    private long responseTime;          //  endpoint response time (number of milliseconds)
    private String errorMessage;        //  exception stack trace or custom error message
    private List<AssertionVerification> assertionVerifications = new ArrayList<AssertionVerification>();
    private TestResult result;

    public Teststep getTeststep() {
        return teststep;
    }

    public void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<AssertionVerification> getAssertionVerifications() {
        return assertionVerifications;
    }

    public void setAssertionVerifications(List<AssertionVerification> assertionVerifications) {
        this.assertionVerifications = assertionVerifications;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }
}
