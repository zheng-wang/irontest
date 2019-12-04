package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.testrun.TeststepRun;

/**
 * Used for displaying single test step run report, by clicking a step in test case run result outline on the test case edit view.
 */
public class TeststepRunView extends View {
    private final TeststepRun teststepRun;
    private IronTestUtilsFreeMarkerAdapter ironTestUtilsAdapter;

    public TeststepRunView(TeststepRun teststepRun) {
        super("../views/teststep/stepRun.ftl");
        this.teststepRun = teststepRun;
        this.ironTestUtilsAdapter = new IronTestUtilsFreeMarkerAdapter();
    }

    public TeststepRun getStepRun() {
        return teststepRun;
    }

    public IronTestUtilsFreeMarkerAdapter getIronTestUtilsAdatper() {
        return ironTestUtilsAdapter;
    }
}
