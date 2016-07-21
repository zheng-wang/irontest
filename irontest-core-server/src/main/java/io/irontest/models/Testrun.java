package io.irontest.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case run.
 * Created by Trevor Li on 7/24/15.
 */
public class Testrun {
    private Long testcaseId;
    private List<Long> failedTeststepIds = new ArrayList<Long>();

    public Testrun() {}

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
}
