package io.irontest.core.runner;

import java.util.Date;

/**
 * Used for passing information across test steps when running a test case.
 */
public class TestcaseRunContext {
    private Date testcaseRunStartTime;
    private Date testcaseIndividualRunStartTime;

    public Date getTestcaseRunStartTime() {
        return testcaseRunStartTime;
    }

    public void setTestcaseRunStartTime(Date testcaseRunStartTime) {
        this.testcaseRunStartTime = testcaseRunStartTime;
    }

    public Date getTestcaseIndividualRunStartTime() {
        return testcaseIndividualRunStartTime;
    }

    public void setTestcaseIndividualRunStartTime(Date testcaseIndividualRunStartTime) {
        this.testcaseIndividualRunStartTime = testcaseIndividualRunStartTime;
    }
}
