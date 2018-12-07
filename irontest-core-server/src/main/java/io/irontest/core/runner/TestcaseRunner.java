package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.HTTPStubHitAssertionProperties;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;
import org.slf4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.irontest.IronTestConstants.*;

public abstract class TestcaseRunner {
    private Testcase testcase;
    private boolean testcaseHasWaitForProcessingCompletionAction = false;
    private UtilsDAO utilsDAO;
    private TestcaseRunDAO testcaseRunDAO;
    private Logger LOGGER;
    private TestcaseRunContext testcaseRunContext = new TestcaseRunContext();
    private Set<String> udpNames;
    private Map<String, String> referenceableStringProperties = new HashMap<>();
    private Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();

    TestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, Logger LOGGER, WireMockServer wireMockServer) {
        this.testcase = testcase;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.LOGGER = LOGGER;
        this.testcaseRunContext.setWireMockServer(wireMockServer);
    }

    protected Testcase getTestcase() {
        return testcase;
    }

    boolean isTestcaseHasWaitForProcessingCompletionAction() {
        return testcaseHasWaitForProcessingCompletionAction;
    }

    TestcaseRunDAO getTestcaseRunDAO() {
        return testcaseRunDAO;
    }

    TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    Set<String> getUdpNames() { return udpNames; }

    Map<String, String> getReferenceableStringProperties() {
        return referenceableStringProperties;
    }

    Map<String, Endpoint> getReferenceableEndpointProperties() {
        return referenceableEndpointProperties;
    }

    public abstract TestcaseRun run() throws JsonProcessingException;

    //  processing before starting the test case run
    void preProcessing() {
        if (!testcase.getHttpStubMappings().isEmpty()) {
            //  add HTTPStubsSetup step
            Teststep httpStubsSetupStep = new Teststep(Teststep.TYPE_HTTP_STUBS_SETUP);
            httpStubsSetupStep.setName("Set up HTTP stubs");
            HTTPStubsSetupTeststepProperties stubsSetupTeststepProperties = new HTTPStubsSetupTeststepProperties();
            stubsSetupTeststepProperties.setHttpStubMappings(testcase.getHttpStubMappings());
            httpStubsSetupStep.setOtherProperties(stubsSetupTeststepProperties);
            testcase.getTeststeps().add(0, httpStubsSetupStep);

            //  add HTTPStubRequestsCheck step
            Teststep stubRequestsCheckStep = new Teststep(Teststep.TYPE_HTTP_STUB_REQUESTS_CHECK);
            stubRequestsCheckStep.setName("Check HTTP stub requests");
            for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
                Assertion stubHitAssertion = new Assertion(Assertion.TYPE_HTTP_STUB_HIT);
                stubHitAssertion.setName("Stub was hit");
                stubHitAssertion.setOtherProperties(
                        new HTTPStubHitAssertionProperties(stub.getNumber(), stub.getExpectedHitCount()));
                stubRequestsCheckStep.getAssertions().add(stubHitAssertion);
            }
            Assertion allStubRequestsMatchedAssertion = new Assertion(Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED);
            allStubRequestsMatchedAssertion.setName("All stub requests were matched");
            stubRequestsCheckStep.getAssertions().add(allStubRequestsMatchedAssertion);
            testcase.getTeststeps().add(testcase.getTeststeps().size(), stubRequestsCheckStep);
        }

        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                //  add Wait step
                testcaseHasWaitForProcessingCompletionAction = true;
                testcase.getTeststeps().add(0, new Teststep(Teststep.TYPE_WAIT));
                break;
            }
        }
    }

    void startTestcaseRun(TestcaseRun testcaseRun) {
        Date testcaseRunStartTime = new Date();
        LOGGER.info("Start running test case: " + testcase.getName());

        testcaseRun.setTestcaseId(testcase.getId());
        testcaseRun.setTestcaseName(testcase.getName());
        testcaseRun.setTestcaseFolderPath(testcase.getFolderPath());
        testcaseRun.setResult(TestResult.PASSED);
        testcaseRun.setStartTime(testcaseRunStartTime);
        testcaseRunContext.setTestcaseRunStartTime(testcaseRunStartTime);

        referenceableStringProperties = IronTestUtils.udpListToMap(testcase.getUdps());
        udpNames = referenceableStringProperties.keySet();
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(testcaseRunStartTime));
    }

    TeststepRun runTeststep(Teststep teststep) {
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
                    teststep, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
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

            //  initially resolve assertion input (based on test step type)
            Object apiResponse = teststepRun.getResponse();
            Object assertionVerificationInput;
            switch (teststep.getType()) {
                case Teststep.TYPE_DB:
                    assertionVerificationInput = ((DBAPIResponse) apiResponse).getRowsJSON();
                break;
                case Teststep.TYPE_MQ:
                    assertionVerificationInput = ((MQAPIResponse) apiResponse).getValue();
                    break;
                default:
                    assertionVerificationInput = apiResponse;
                    break;
            }

            if (Teststep.TYPE_DB.equals(teststep.getType()) && assertionVerificationInput == null) {
                //  SQL inserts/deletes/updates, no assertion verification needed
            } else {
                Object assertionVerificationInput2 = null;

                //  verify assertions against the inputs
                for (Assertion assertion : teststep.getAssertions()) {
                    //  further resolve assertion inputs
                    if (Assertion.TYPE_STATUS_CODE_EQUAL.equals(assertion.getType())) {
                        assertionVerificationInput = ((HTTPAPIResponse) apiResponse).getStatusCode();
                    } else if (Teststep.TYPE_SOAP.equals(teststep.getType()) || Teststep.TYPE_HTTP.equals(teststep.getType())) {
                        assertionVerificationInput = ((HTTPAPIResponse) apiResponse).getHttpBody();
                    } else if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertion.getType())) {
                        assertionVerificationInput = ((WireMockServerAPIResponse) apiResponse).getAllServeEvents();
                        HTTPStubHitAssertionProperties otherProperties = (HTTPStubHitAssertionProperties) assertion.getOtherProperties();
                        assertionVerificationInput2 = getTestcaseRunContext().getHttpStubMappingInstanceIds().get(otherProperties.getStubNumber());
                    }

                    AssertionVerification verification = new AssertionVerification();
                    teststepRun.getAssertionVerifications().add(verification);
                    verification.setAssertion(assertion);

                    AssertionVerifier verifier = AssertionVerifierFactory.getInstance().create(
                            assertion.getType(), referenceableStringProperties);
                    AssertionVerificationResult verificationResult;
                    try {
                        verificationResult = verifier.verify(assertion, assertionVerificationInput, assertionVerificationInput2);
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
