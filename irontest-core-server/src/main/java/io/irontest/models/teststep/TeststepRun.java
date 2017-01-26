package io.irontest.models.teststep;

import io.irontest.core.runner.BasicTeststepRun;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used for test case running.
 * Created by Zheng on 24/07/2016.
 */
public class TeststepRun extends BasicTeststepRun {
    private Teststep teststep;
    private Date startTime;
    private long duration;              //  number of milliseconds
    private String errorMessage;        //  error message of running the test step (errors when verifying assertions are captured in AssertionVerification)
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

    public void importBasicTeststepRun(BasicTeststepRun basicTeststepRun) {
        setResponse(basicTeststepRun.getResponse());
        setInfoMessage(basicTeststepRun.getInfoMessage());
    }
}
