package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.testrun.TeststepRun;

/**
 * Created by Zheng on 1/08/2016.
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
