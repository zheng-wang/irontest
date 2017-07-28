package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.runner.*;
import io.irontest.db.TestcaseDAO;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.TestcaseRun;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRun;
import io.irontest.models.teststep.WaitTeststepProperties;
import io.irontest.views.TestcaseRunView;
import io.irontest.views.TeststepRunView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testcaseruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestcaseRunResource.class);
    private final TestcaseDAO testcaseDAO;
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;
    private final TestcaseRunDAO testcaseRunDAO;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO,
                               TestcaseRunDAO testcaseRunDAO) {
        this.testcaseDAO = testcaseDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
    }

    @POST
    public TestcaseRun create(TestcaseRun testcaseRun) throws JsonProcessingException {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseRun.getTestcase().getId());

        preProcessingForIIBTeststep(testcase);

        testcaseRun.setTestcase(testcase);

        //  test case run starts
        TestcaseRunContext testcaseRunContext = new TestcaseRunContext();
        Date startTime = new Date();
        testcaseRun.setResult(TestResult.PASSED);
        testcaseRun.setStartTime(startTime);
        testcaseRunContext.setTestcaseRunStartTime(startTime);

        for (Teststep teststep : testcase.getTeststeps()) {
            TeststepRun stepRun = new TeststepRun();
            testcaseRun.getStepRuns().add(stepRun);
            stepRun.setTeststep(teststep);

            //  test step run starts
            stepRun.setStartTime(new Date());
            LOGGER.info("Start running test step: " + teststep.getName());

            //  run test step
            BasicTeststepRun basicTeststepRun = null;
            boolean exceptionOccurred = false;  //  use this flag instead of checking stepRun.getErrorMessage() != null, for code clarity
            try {
                basicTeststepRun = TeststepRunnerFactory.getInstance()
                        .newTeststepRunner(teststep, teststepDAO, utilsDAO, testcaseRunContext).run();
                LOGGER.info("API invocation response: " +
                        (basicTeststepRun.getResponse() == null ? null : basicTeststepRun.getResponse().toString()));
                stepRun.importBasicTeststepRun(basicTeststepRun);
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
                Object assertionVerificationInput = null;
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

                        AssertionVerifier verifier = new AssertionVerifierFactory().create(assertion.getType());
                        AssertionVerificationResult verificationResult = null;
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
        testcaseRunDAO.insert(testcaseRun);

        //  prepare return object for UI (reduced contents for performance)
        testcaseRun.setTestcase(null);
        List<Long> failedTeststepIds = new ArrayList<Long>();
        for (TeststepRun stepRun : testcaseRun.getStepRuns()) {
            if (TestResult.FAILED == stepRun.getResult()) {
                failedTeststepIds.add(stepRun.getTeststep().getId());
            }
        }
        testcaseRun.setFailedTeststepIds(failedTeststepIds);
        testcaseRun.getStepRuns().clear();
        return testcaseRun;
    }

    private void preProcessingForIIBTeststep(Testcase testcase) {
        boolean testcaseHasWaitForProcessingCompletionAction = false;
        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                testcaseHasWaitForProcessingCompletionAction = true;
            }
        }
        if (testcaseHasWaitForProcessingCompletionAction) {
            Teststep waitStep = new Teststep();
            waitStep.setName("Wait 1 second");
            waitStep.setType(Teststep.TYPE_WAIT);
            waitStep.setOtherProperties(new WaitTeststepProperties(1));
            testcase.getTeststeps().add(0, waitStep);
        }
    }

    @GET @Path("{testcaseRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TestcaseRunView getHTMLReportByTestcaseRunId(@PathParam("testcaseRunId") long testcaseRunId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findById(testcaseRunId);
        return new TestcaseRunView(testcaseRun);
    }

    @GET @Path("{testcaseRunId}/stepruns/{teststepId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TeststepRunView getStepRunHTMLReportByTeststepId(@PathParam("testcaseRunId") long testcaseRunId,
                                                            @PathParam("teststepId") long teststepId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findById(testcaseRunId);
        TeststepRun theStepRun = null;
        for (TeststepRun stepRun : testcaseRun.getStepRuns()) {
            if (stepRun.getTeststep().getId() == teststepId) {
                theStepRun = stepRun;
                break;
            }
        }
        return new TeststepRunView(theStepRun);
    }
}