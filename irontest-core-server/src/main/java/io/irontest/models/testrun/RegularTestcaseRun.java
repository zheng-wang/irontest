package io.irontest.models.testrun;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 9/03/2018.
 */
public class RegularTestcaseRun extends TestcaseRun {
    private List<TeststepRun> stepRuns = new ArrayList<>();

    //  only used on UI
    private List<Long> failedTeststepIds = new ArrayList<>();

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
