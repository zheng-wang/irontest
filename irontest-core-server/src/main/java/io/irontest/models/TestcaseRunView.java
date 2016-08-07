package io.irontest.models;

import io.dropwizard.views.View;

/**
 * Created by Zheng on 1/08/2016.
 */
public class TestcaseRunView extends View {
    private final TestcaseRun testcaseRun;

    public TestcaseRunView(TestcaseRun testcaseRun) {
        super("testcaseRun.ftl");
        this.testcaseRun = testcaseRun;
    }

    public TestcaseRun getTestcaseRun() {
        return testcaseRun;
    }
}
