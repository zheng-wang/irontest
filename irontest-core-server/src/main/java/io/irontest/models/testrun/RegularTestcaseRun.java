package io.irontest.models.testrun;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 9/03/2018.
 */
public class RegularTestcaseRun extends TestcaseRun {
    private List<TeststepRun> stepRuns = new ArrayList<>();

    public List<TeststepRun> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<TeststepRun> stepRuns) {
        this.stepRuns = stepRuns;
    }
}
