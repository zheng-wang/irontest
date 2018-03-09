package io.irontest.models;

import io.irontest.models.teststep.TeststepRun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used for collecting data when running a test case.
 * Created by Trevor Li on 7/24/15.
 */
public class TestcaseRun {
    private Long id;
    private long testcaseId;
    private String testcaseName;
    private String testcaseFolderPath;
    private Date startTime;
    private long duration;              //  in milliseconds
    private List<TeststepRun> stepRuns = new ArrayList<TeststepRun>();
    private TestResult result;

    //  only used on UI
    private List<Long> failedTeststepIds = new ArrayList<Long>();

    public TestcaseRun() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTestcaseFolderPath() {
        return testcaseFolderPath;
    }

    public void setTestcaseFolderPath(String testcaseFolderPath) {
        this.testcaseFolderPath = testcaseFolderPath;
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
