package io.irontest.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.propertyextractor.PropertyExtractorRunner;
import io.irontest.core.propertyextractor.PropertyExtractorRunnerFactory;
import io.irontest.core.teststep.*;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.assertion.*;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.propertyextractor.PropertyExtractor;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.HTTPHeader;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

import static io.irontest.IronTestConstants.*;

public abstract class TestcaseRunner {
    private Testcase testcase;
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

    Testcase getTestcase() {
        return testcase;
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

    public abstract TestcaseRun run() throws IOException;

    //  process the test case before starting to run it
    void preProcessing() {
        if (!testcase.getHttpStubMappings().isEmpty()) {
            //  add HTTPStubsSetup step
            Teststep httpStubsSetupStep = new Teststep(Teststep.TYPE_HTTP_STUBS_SETUP);
            httpStubsSetupStep.setName("Set up HTTP stubs");
            HTTPStubsSetupTeststepProperties stubsSetupTeststepProperties = new HTTPStubsSetupTeststepProperties();
            stubsSetupTeststepProperties.setHttpStubMappings(testcase.getHttpStubMappings());
            httpStubsSetupStep.setOtherProperties(stubsSetupTeststepProperties);
            testcase.getTeststeps().add(0, httpStubsSetupStep);

            //  add HTTPStubRequestsCheck step and its assertions
            Teststep stubRequestsCheckStep = new Teststep(Teststep.TYPE_HTTP_STUB_REQUESTS_CHECK);
            testcase.getTeststeps().add(testcase.getTeststeps().size(), stubRequestsCheckStep);
            stubRequestsCheckStep.setName("Check HTTP stub requests");
            for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
                Assertion stubHitAssertion = new Assertion(Assertion.TYPE_HTTP_STUB_HIT);
                stubHitAssertion.setName("Stub was hit");
                stubHitAssertion.setOtherProperties(
                        new HTTPStubHitAssertionProperties(stub.getNumber(), stub.getExpectedHitCount()));
                stubRequestsCheckStep.getAssertions().add(stubHitAssertion);
            }
            if (testcase.getHttpStubMappings().size() > 1 && testcase.isCheckHTTPStubsHitOrder()) {
                Assertion stubsHitInOrderAssertion = new Assertion(Assertion.TYPE_HTTP_STUBS_HIT_IN_ORDER);
                stubsHitInOrderAssertion.setName("Stubs were hit in order");
                List<Short> expectedHitOrder = new ArrayList<>();
                for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
                    expectedHitOrder.add(stub.getNumber());
                }
                stubsHitInOrderAssertion.setOtherProperties(new HTTPStubsHitInOrderAssertionProperties(expectedHitOrder));
                stubRequestsCheckStep.getAssertions().add(stubsHitInOrderAssertion);
            }
            Assertion allStubRequestsMatchedAssertion = new Assertion(Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED);
            allStubRequestsMatchedAssertion.setName("All stub requests were matched");
            stubRequestsCheckStep.getAssertions().add(allStubRequestsMatchedAssertion);
        }

        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                Teststep waitUntilNextSecondStep = new Teststep(Teststep.TYPE_WAIT_UNTIL_NEXT_SECOND);
                waitUntilNextSecondStep.setName("Wait until next second");
                testcase.getTeststeps().add(0, waitUntilNextSecondStep);
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

    TeststepRun runTeststep(Teststep teststep) throws IOException {
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

        if (exceptionOccurred) {
            teststepRun.setResult(TestResult.FAILED);
        } else {
            teststepRun.setResult(TestResult.PASSED);
            Object apiResponse = teststepRun.getResponse();

            verifyAssertions(teststep.getType(), teststep.getAction(), teststep.getAssertions(), apiResponse, teststepRun);

            Map<String, String> extractedProperties = new HashMap<>();
            try {
                extractedProperties = extractPropertiesOutOfAPIResponse(teststep.getType(),
                        teststep.getPropertyExtractors(), apiResponse, referenceableStringProperties);
            } catch (Exception e) {
                String errorMessage = "Failed to extract properties out of API response.";
                LOGGER.error(errorMessage, e);
                teststepRun.setErrorMessage(errorMessage + " " + e.getMessage());
                teststepRun.setResult(TestResult.FAILED);
            }
            referenceableStringProperties.putAll(extractedProperties);
        }

        //  test step run ends
        teststepRun.setDuration(new Date().getTime() - teststepRun.getStartTime().getTime());

        return teststepRun;
    }

    private Object resolveAssertionVerificationInputFromAPIResponse(String teststepType, String teststepAction,
                                                                    String assertionType, Object apiResponse) {
        Object result = apiResponse;

        if (Assertion.TYPE_STATUS_CODE_EQUAL.equals(assertionType)) {
            result = ((HTTPAPIResponse) apiResponse).getStatusCode();
        } else if (Teststep.TYPE_SOAP.equals(teststepType) || Teststep.TYPE_HTTP.equals(teststepType)) {
            result = ((HTTPAPIResponse) apiResponse).getHttpBody();
        } else if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertionType) ||
                Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED.equals(assertionType) ||
                Assertion.TYPE_HTTP_STUBS_HIT_IN_ORDER.equals(assertionType)) {
            result = ((WireMockServerAPIResponse) apiResponse).getAllServeEvents();
        } else if (Teststep.TYPE_DB.equals(teststepType)) {
            result = ((DBAPIResponse) apiResponse).getRowsJSON();
        } else if (Teststep.TYPE_JMS.equals(teststepType)) {
            if (Teststep.ACTION_CHECK_DEPTH.equals(teststepAction)) {
                result = ((JMSCheckQueueDepthResponse) apiResponse).getQueueDepth();
            } else if (Teststep.ACTION_BROWSE.equals(teststepAction)) {
                result = apiResponse == null ? null : ((JMSBrowseQueueResponse) apiResponse).getBody();
            }
        } else if (Teststep.TYPE_MQ.equals(teststepType)) {
            if (Teststep.ACTION_CHECK_DEPTH.equals(teststepAction)) {
                result = ((MQCheckQueueDepthResponse) apiResponse).getQueueDepth();
            } else if (Teststep.ACTION_DEQUEUE.equals(teststepAction)) {
                MQDequeueResponse mqDequeueResponse = (MQDequeueResponse) apiResponse;
                if (mqDequeueResponse == null) {
                    result = null;
                } else {
                    if (Assertion.TYPE_HAS_AN_MQRFH2_FOLDER_EQUAL_TO_XML.equals(assertionType)) {
                        result = mqDequeueResponse.getMqrfh2Header();
                    } else {
                        result = mqDequeueResponse.getBodyAsText();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Verify assertions against the API response.
     * @param teststepType
     * @param teststepAction
     * @param assertions
     * @param apiResponse
     * @param teststepRun
     */
    private void verifyAssertions(String teststepType, String teststepAction, List<Assertion> assertions,
                                  Object apiResponse, TeststepRun teststepRun) throws IOException {
        for (Assertion assertion : assertions) {
            Object assertionVerificationInput = resolveAssertionVerificationInputFromAPIResponse(teststepType,
                    teststepAction, assertion.getType(), apiResponse);

            //  resolve assertion verification input2 if applicable
            Object assertionVerificationInput2 = null;
            if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertion.getType())) {
                HTTPStubHitAssertionProperties otherProperties = (HTTPStubHitAssertionProperties) assertion.getOtherProperties();
                assertionVerificationInput2 = getTestcaseRunContext().getHttpStubMappingInstanceIds().get(otherProperties.getStubNumber());
            }

            AssertionVerification verification = new AssertionVerification();
            teststepRun.getAssertionVerifications().add(verification);
            verification.setAssertion(assertion);

            AssertionVerifier verifier = AssertionVerifierFactory.getInstance().create(
                    assertion, referenceableStringProperties);
            AssertionVerificationResult verificationResult;
            try {
                verificationResult = verifier.verify(assertionVerificationInput, assertionVerificationInput2);
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

    /**
     * Extract properties out of API response, and make the properties visible to the next test step run.
     */
    private Map<String, String> extractPropertiesOutOfAPIResponse(String teststepType,
                                                                  List<PropertyExtractor> propertyExtractors,
                                                                  Object apiResponse,
                                                                  Map<String, String> referenceableStringProperties) throws Exception {
        Map<String, String> extractedProperties = new HashMap<>();
        for (PropertyExtractor propertyExtractor: propertyExtractors) {
            String propertyExtractionInput = null;
            if (Teststep.TYPE_HTTP.equals(teststepType)) {
                HTTPAPIResponse httpApiResponse = (HTTPAPIResponse) apiResponse;
                if (PropertyExtractor.TYPE_COOKIE.equals(propertyExtractor.getType())) {
                    Optional<HTTPHeader> setCookieHeader = httpApiResponse.getHttpHeaders().stream()
                            .filter(httpHeader -> HttpHeader.SET_COOKIE.asString().equals(httpHeader.getName())).findFirst();
                    propertyExtractionInput = setCookieHeader.isPresent() ? setCookieHeader.get().getValue() : null;
                } else {
                    propertyExtractionInput = httpApiResponse.getHttpBody();
                }
            }

            PropertyExtractorRunner propertyExtractorRunner = PropertyExtractorRunnerFactory.getInstance().create(
                    propertyExtractor, referenceableStringProperties);
            String propertyValue = propertyExtractorRunner.extract(propertyExtractionInput);
            extractedProperties.put(propertyExtractor.getPropertyName(), propertyValue);
        }

        return extractedProperties;
    }
}
