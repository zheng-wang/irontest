package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.WaitTeststepProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class RegularTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularTestcaseRunner.class);

    public RegularTestcaseRunner(Testcase testcase, TeststepDAO teststepDAO,
                                 UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        super(testcase, teststepDAO, utilsDAO, testcaseRunDAO, LOGGER);
    }

    @Override
    public TestcaseRun run() throws JsonProcessingException {
        RegularTestcaseRun testcaseRun = new RegularTestcaseRun();

        preProcessingForIIBTestcase();
        startTestcaseRun(testcaseRun);
        if (isTestcaseHasWaitForProcessingCompletionAction()) {
            long secondFraction = getTestcaseRunContext().getTestcaseRunStartTime().getTime() % 1000;   //  milliseconds
            long millisecondsUntilNextSecond = 1000 - secondFraction;
            Teststep waitStep = getTestcase().getTeststeps().get(0);
            waitStep.setName("Wait " + millisecondsUntilNextSecond + " milliseconds");
            waitStep.setOtherProperties(new WaitTeststepProperties(millisecondsUntilNextSecond));
        }

        //  run test steps
        for (Teststep teststep : getTestcase().getTeststeps()) {
            testcaseRun.getStepRuns().add(runTeststep(teststep));
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());
        LOGGER.info("Finish running test case: " + getTestcase().getName());
        for (TeststepRun teststepRun: testcaseRun.getStepRuns()) {
            if (TestResult.FAILED == teststepRun.getResult()) {
                testcaseRun.setResult(TestResult.FAILED);
                break;
            }
        }

        //  persist test case run details into database
        getTestcaseRunDAO().insert(testcaseRun);

        return testcaseRun;
    }
}
