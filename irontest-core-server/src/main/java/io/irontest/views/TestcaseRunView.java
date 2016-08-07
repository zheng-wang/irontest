package io.irontest.views;

import io.dropwizard.views.View;
import io.irontest.models.TestcaseRun;

/**
 * Created by Zheng on 1/08/2016.
 */
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
