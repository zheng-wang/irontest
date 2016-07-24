package io.irontest.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 7/24/15.
 */
public class TestcaseRun {
    private long testcaseId;
    private String testcaseName;
    private Date startTime;
    private long duration;              //  number of milliseconds
    private TestResult result;
    private List<TeststepRun> stepRuns = new ArrayList<TeststepRun>();

    private List<Long> failedTeststepIds = new ArrayList<Long>();

    public TestcaseRun() {}

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getTestcaseName() {
        return testcaseName;
    }

    public void setTestcaseName(String testcaseName) {
        this.testcaseName = testcaseName;
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

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public List<TeststepRun> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<TeststepRun> stepRuns) {
        this.stepRuns = stepRuns;
    }

    public List<Long> getFailedTeststepIds() {
        return failedTeststepIds;
    }

    public void setFailedTeststepIds(List<Long> failedTeststepIds) {
        this.failedTeststepIds = failedTeststepIds;
    }
}
