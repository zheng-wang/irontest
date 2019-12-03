package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.testrun.TestcaseRun;

/**
 * Used for displaying test case run report.
 */
public class TestcaseRunView extends View {
    private final TestcaseRun testcaseRun;
    private IronTestUtilsFreeMarkerAdapter ironTestUtilsAdapter;

    public TestcaseRunView(TestcaseRun testcaseRun) {
        super("../views/testcaseRun.ftl");
        this.testcaseRun = testcaseRun;
        this.ironTestUtilsAdapter = new IronTestUtilsFreeMarkerAdapter();
    }

    public TestcaseRun getTestcaseRun() {
        return testcaseRun;
    }

    public IronTestUtilsFreeMarkerAdapter getIronTestUtilsAdatper() {
        return ironTestUtilsAdapter;
    }
}
