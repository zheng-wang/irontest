package io.irontest.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public class RegularTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularTestcaseRunner.class);

    public RegularTestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, WireMockServer wireMockServer) {
        super(testcase, utilsDAO, testcaseRunDAO, LOGGER, wireMockServer);
    }

    @Override
    public TestcaseRun run() throws IOException {
        RegularTestcaseRun testcaseRun = new RegularTestcaseRun();

        preProcessing();
        startTestcaseRun(testcaseRun);

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
