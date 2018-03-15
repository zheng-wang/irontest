package io.irontest.core.runner;

import io.irontest.db.*;
import io.irontest.models.testrun.TestcaseRun;

/**
 * Created by Zheng on 15/03/2018.
 */
public class DataDrivenTestcaseRunner extends TestcaseRunner {
    public DataDrivenTestcaseRunner(long testcaseId, TestcaseDAO testcaseDAO, UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        super(testcaseId, testcaseDAO, udpDAO, teststepDAO, utilsDAO, testcaseRunDAO);
    }

    @Override
    public TestcaseRun run() {
        return null;
    }
}
