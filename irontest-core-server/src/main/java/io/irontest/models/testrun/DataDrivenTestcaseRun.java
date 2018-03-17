package io.irontest.models.testrun;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 9/03/2018.
 */
public class DataDrivenTestcaseRun extends TestcaseRun {
    private List<TestcaseIndividualRun> individualRuns = new ArrayList<>();

    public List<TestcaseIndividualRun> getIndividualRuns() {
        return individualRuns;
    }

    public void setIndividualRuns(List<TestcaseIndividualRun> individualRuns) {
        this.individualRuns = individualRuns;
    }
}
