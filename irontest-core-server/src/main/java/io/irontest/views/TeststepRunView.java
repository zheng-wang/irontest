package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.testrun.TeststepRun;

/**
 * Used for displaying single test step run report, by clicking a step in test case run result outline on the test case edit view.
 */
public class TeststepRunView extends View {
    private final TeststepRun teststepRun;

    public TeststepRunView(TeststepRun teststepRun) {
        super("../views/teststep/stepRun.ftl");
        this.teststepRun = teststepRun;
    }

    public TeststepRun getStepRun() {
        return teststepRun;
    }
}
