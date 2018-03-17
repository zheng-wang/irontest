package io.irontest.models.testrun;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual run of a data driven test case, corresponding to one row in the data table.
 *
 * Created by Zheng on 9/03/2018.
 */
public class TestcaseIndividualRun extends TestRun {
    private String caption;      //  caption of the data table row
    private List<TeststepRun> stepRuns = new ArrayList<>();

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<TeststepRun> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<TeststepRun> stepRuns) {
        this.stepRuns = stepRuns;
    }
}
