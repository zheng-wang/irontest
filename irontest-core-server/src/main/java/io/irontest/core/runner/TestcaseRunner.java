package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 15/03/2018.
 */
public abstract class TestcaseRunner {
    private Testcase testcase;
    private boolean testcaseHasWaitForProcessingCompletionAction = false;
    private List<UserDefinedProperty> testcaseUDPs;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;
    private TestcaseRunDAO testcaseRunDAO;
    private Logger LOGGER;
    private TestcaseRunContext testcaseRunContext = new TestcaseRunContext();
    private Map<String, String> referenceableStringProperties = new HashMap<>();
    private Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();

    protected TestcaseRunner(Testcase testcase, List<UserDefinedProperty> testcaseUDPs, TeststepDAO teststepDAO,
                             UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, Logger LOGGER) {
        this.testcase = testcase;
        this.testcaseUDPs = testcaseUDPs;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.LOGGER = LOGGER;
    }

    protected Testcase getTestcase() {
        return testcase;
    }

    public boolean isTestcaseHasWaitForProcessingCompletionAction() {
        return testcaseHasWaitForProcessingCompletionAction;
    }

    protected TestcaseRunDAO getTestcaseRunDAO() {
        return testcaseRunDAO;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    protected Map<String, String> getReferenceableStringProperties() {
        return referenceableStringProperties;
    }

    protected Map<String, Endpoint> getReferenceableEndpointProperties() {
        return referenceableEndpointProperties;
    }

    public abstract TestcaseRun run() throws JsonProcessingException;

    protected void preProcessingForIIBTestcase() {
        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                testcaseHasWaitForProcessingCompletionAction = true;
            }
        }
        if (testcaseHasWaitForProcessingCompletionAction) {
            Teststep waitStep = new Teststep();
            waitStep.setType(Teststep.TYPE_WAIT);
            testcase.getTeststeps().add(0, waitStep);
        }
    }

    protected void startTestcaseRun(TestcaseRun testcaseRun) {
        Date testcaseRunStartTime = new Date();
        LOGGER.info("Start running test case: " + testcase.getName());

        testcaseRun.setTestcaseId(testcase.getId());
        testcaseRun.setTestcaseName(testcase.getName());
        testcaseRun.setTestcaseFolderPath(testcase.getFolderPath());
        testcaseRun.setResult(TestResult.PASSED);
        testcaseRun.setStartTime(testcaseRunStartTime);
        testcaseRunContext.setTestcaseRunStartTime(testcaseRunStartTime);

        referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(testcaseRunStartTime));
    }

    protected TeststepRun runTeststep(Teststep teststep) {
        TeststepRun teststepRun = new TeststepRun();
        teststepRun.setTeststep(teststep);

        //  test step run starts
        Date teststepRunStartTime = new Date();
        teststepRun.setStartTime(teststepRunStartTime);
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(teststepRunStartTime));
        LOGGER.info("Start running test step: " + teststep.getName());

        //  run test step
        BasicTeststepRun basicTeststepRun;
        boolean exceptionOccurred = false;  //  use this flag instead of checking stepRun.getErrorMessage() != null, for code clarity
        try {
            basicTeststepRun = TeststepRunnerFactory.getInstance().newTeststepRunner(
                    teststep, teststepDAO, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
                    testcaseRunContext).run();
            LOGGER.info("Finish running test step: " + teststep.getName());
            teststepRun.setResponse(basicTeststepRun.getResponse());
            teststepRun.setInfoMessage(basicTeststepRun.getInfoMessage());
        } catch (Exception e) {
            exceptionOccurred = true;
            String message = e.getMessage();
            teststepRun.setErrorMessage(message == null ? "null" : message);  // exception message could be null (though rarely)
            LOGGER.error(message, e);
        }

        //  verify assertions
        if (exceptionOccurred) {
            teststepRun.setResult(TestResult.FAILED);
        } else {
            teststepRun.setResult(TestResult.PASSED);

            //  get input for assertion verifications
            Object apiResponse = teststepRun.getResponse();
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
                    teststepRun.getAssertionVerifications().add(verification);
                    verification.setAssertion(assertion);

                    AssertionVerifier verifier = AssertionVerifierFactory.getInstance().create(
                            assertion.getType(), referenceableStringProperties);
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
                        teststepRun.setResult(TestResult.FAILED);
                    }
                }
            }
        }

        //  test step run ends
        teststepRun.setDuration(new Date().getTime() - teststepRun.getStartTime().getTime());

        return teststepRun;
    }
}
