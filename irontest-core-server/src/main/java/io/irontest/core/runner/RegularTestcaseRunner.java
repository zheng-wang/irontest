package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.WaitTeststepProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 15/03/2018.
 */
public class RegularTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularTestcaseRunner.class);

    public RegularTestcaseRunner(Testcase testcase, List<UserDefinedProperty> testcaseUDPs, TeststepDAO teststepDAO,
                                 UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        super(testcase, testcaseUDPs, teststepDAO, utilsDAO, testcaseRunDAO, LOGGER);
    }

    @Override
    public TestcaseRun run() throws JsonProcessingException {
        RegularTestcaseRun testcaseRun = new RegularTestcaseRun();
        startTestcaseRun(testcaseRun);
        preProcessingForIIBTeststep(getTestcase(), getTestcaseRunContext().getTestcaseRunStartTime());

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

        //  prepare return object for UI (reduced contents for performance)
        List<TeststepRun> teststepRunsForUI = new ArrayList<>();
        for (TeststepRun stepRun : testcaseRun.getStepRuns()) {
            TeststepRun teststepRunForUI = new TeststepRun();
            teststepRunForUI.setId(stepRun.getId());
            teststepRunForUI.setResult(stepRun.getResult());
            Teststep teststepForUI = new Teststep();
            teststepForUI.setId(stepRun.getTeststep().getId());
            teststepRunForUI.setTeststep(teststepForUI);
            teststepRunsForUI.add(teststepRunForUI);
        }
        RegularTestcaseRun testcaseRunForUI = new RegularTestcaseRun();
        testcaseRunForUI.setId(testcaseRun.getId());
        testcaseRunForUI.setResult(testcaseRun.getResult());
        testcaseRunForUI.setStepRuns(teststepRunsForUI);
        return testcaseRunForUI;
    }

    private void preProcessingForIIBTeststep(Testcase testcase, Date testcaseRunStartTime) {
        long secondFraction = testcaseRunStartTime.getTime() % 1000;   //  milliseconds
        long millisecondsUntilNextSecond = 1000 - secondFraction;
        boolean testcaseHasWaitForProcessingCompletionAction = false;
        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                testcaseHasWaitForProcessingCompletionAction = true;
            }
        }
        if (testcaseHasWaitForProcessingCompletionAction) {
            Teststep waitStep = new Teststep();
            waitStep.setName("Wait " + millisecondsUntilNextSecond + " milliseconds");
            waitStep.setType(Teststep.TYPE_WAIT);
            waitStep.setOtherProperties(new WaitTeststepProperties(millisecondsUntilNextSecond));
            testcase.getTeststeps().add(0, waitStep);
        }
    }
}
