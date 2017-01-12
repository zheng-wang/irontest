package io.irontest.core.runner;

import java.util.Date;

/**
 * Used for passing information across test steps when running a test case.
 * Created by zhenw9 on 12/01/2017.
 */
public class TestcaseRunContext {
    private Date testcaseRunStartTime;

    public Date getTestcaseRunStartTime() {
        return testcaseRunStartTime;
    }

    public void setTestcaseRunStartTime(Date testcaseRunStartTime) {
        this.testcaseRunStartTime = testcaseRunStartTime;
    }
}
