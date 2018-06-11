package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.testrun.TestcaseRun;

public class TestcaseRunView extends View {
    private final TestcaseRun testcaseRun;

    public TestcaseRunView(TestcaseRun testcaseRun) {
        super("../views/testcaseRun.ftl");
        this.testcaseRun = testcaseRun;
    }

    public TestcaseRun getTestcaseRun() {
        return testcaseRun;
    }
}
