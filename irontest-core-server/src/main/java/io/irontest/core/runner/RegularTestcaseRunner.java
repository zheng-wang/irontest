package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.*;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.WaitTeststepProperties;
import io.irontest.utils.IronTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 15/03/2018.
 */
public class RegularTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularTestcaseRunner.class);

    public RegularTestcaseRunner(long testcaseId, TestcaseDAO testcaseDAO, UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        super(testcaseId, testcaseDAO, udpDAO, teststepDAO, utilsDAO, testcaseRunDAO);
    }

    @Override
    public TestcaseRun run() throws JsonProcessingException {
        RegularTestcaseRun testcaseRun = new RegularTestcaseRun();
        testcaseRun.setTestcaseId(getTestcaseId());
        List<UserDefinedProperty> testcaseUDPs = getUdpDAO().findByTestcaseId(getTestcaseId());
        Testcase testcase = getTestcaseDAO().findById_Complete(getTestcaseId());
        testcaseRun.setTestcaseName(testcase.getName());
        testcaseRun.setTestcaseFolderPath(testcase.getFolderPath());

        //  test case run starts
        Date testcaseRunStartTime = new Date();
        TestcaseRunContext testcaseRunContext = new TestcaseRunContext();
        preProcessingForIIBTeststep(testcase, testcaseRunStartTime);
        testcaseRun.setResult(TestResult.PASSED);
        testcaseRun.setStartTime(testcaseRunStartTime);
        testcaseRunContext.setTestcaseRunStartTime(testcaseRunStartTime);
        Map<String, String> referenceableProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        SimpleDateFormat implicitPropertyDateTimeFormat = new SimpleDateFormat(IMPLICIT_PROPERTY_DATE_TIME_FORMAT);
        referenceableProperties.put(IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME,
                implicitPropertyDateTimeFormat.format(testcaseRunStartTime));

        for (Teststep teststep : testcase.getTeststeps()) {
            TeststepRun stepRun = new TeststepRun();
            testcaseRun.getStepRuns().add(stepRun);
            stepRun.setTeststep(teststep);

            //  test step run starts
            Date teststepRunStartTime = new Date();
            stepRun.setStartTime(teststepRunStartTime);
            referenceableProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                    implicitPropertyDateTimeFormat.format(teststepRunStartTime));
            LOGGER.info("Start running test step: " + teststep.getName());

            //  run test step
            BasicTeststepRun basicTeststepRun;
            boolean exceptionOccurred = false;  //  use this flag instead of checking stepRun.getErrorMessage() != null, for code clarity
            try {
                basicTeststepRun = TeststepRunnerFactory.getInstance()
                        .newTeststepRunner(teststep, getTeststepDAO(), getUtilsDAO(), referenceableProperties, testcaseRunContext).run();
                LOGGER.info("Finish running test step: " + teststep.getName());
                stepRun.setResponse(basicTeststepRun.getResponse());
                stepRun.setInfoMessage(basicTeststepRun.getInfoMessage());
            } catch (Exception e) {
                exceptionOccurred = true;
                String message = e.getMessage();
                stepRun.setErrorMessage(message == null ? "null" : message);  // exception message could be null (though rarely)
                LOGGER.error(message, e);
            }

            //  verify assertions
            if (exceptionOccurred) {
                stepRun.setResult(TestResult.FAILED);
            } else {
                stepRun.setResult(TestResult.PASSED);

                //  get input for assertion verifications
                Object apiResponse = stepRun.getResponse();
                Object assertionVerificationInput;
                if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
                    assertionVerificationInput = ((SOAPAPIResponse) apiResponse).getHttpBody();
                } else if (Teststep.TYPE_DB.equals(teststep.getType())) {
                    assertionVerificationInput = ((DBAPIResponse) apiResponse).getRowsJSON();
                } else if (Teststep.TYPE_MQ.equals(teststep.getType())) {
                    assertionVerificationInput = ((MQAPIResponse) apiResponse).getValue();
                } else {
                    assertionVerificationInput = apiResponse;
                }

                if (Teststep.TYPE_DB.equals(teststep.getType()) && assertionVerificationInput == null) {
                    //  SQL inserts/deletes/updates, no assertion verification needed
                } else {
                    //  verify assertions against the input
                    for (Assertion assertion : teststep.getAssertions()) {
                        AssertionVerification verification = new AssertionVerification();
                        stepRun.getAssertionVerifications().add(verification);
                        verification.setAssertion(assertion);

                        AssertionVerifier verifier = AssertionVerifierFactory.getInstance().create(
                                assertion.getType(), referenceableProperties);
                        AssertionVerificationResult verificationResult;
                        try {
                            verificationResult = verifier.verify(assertion, assertionVerificationInput);
                        } catch (Exception e) {
                            LOGGER.error("Failed to verify assertion", e);
                            verificationResult = new AssertionVerificationResult();
                            verificationResult.setResult(TestResult.FAILED);
                            String message = e.getMessage();
                            verificationResult.setError(message == null ? "null" : message);  // exception message could be null (though rarely)
                        }

                        verification.setVerificationResult(verificationResult);

                        if (TestResult.FAILED == verificationResult.getResult()) {
                            stepRun.setResult(TestResult.FAILED);
                        }
                    }
                }
            }

            //  test step run ends
            stepRun.setDuration(new Date().getTime() - stepRun.getStartTime().getTime());
            if (TestResult.FAILED == stepRun.getResult()) {
                testcaseRun.setResult(TestResult.FAILED);
            }
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());

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
