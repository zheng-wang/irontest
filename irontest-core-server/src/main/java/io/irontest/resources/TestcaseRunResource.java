package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.runner.MQTeststepRunResult;
import io.irontest.core.runner.SOAPTeststepRunResult;
import io.irontest.core.runner.TeststepRunnerFactory;
import io.irontest.db.TestcaseDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.*;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testcaseruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestcaseRunResource.class);
    private final TestcaseDAO testcaseDAO;
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.testcaseDAO = testcaseDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public TestcaseRun create(TestcaseRun testcaseRun) throws Exception {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseRun.getTestcase().getId());
        testcaseRun.setTestcase(testcase);

        //  test case run starts
        testcaseRun.setStartTime(new Date());

        for (Teststep teststep : testcase.getTeststeps()) {
            TeststepRun stepRun = new TeststepRun();
            testcaseRun.getStepRuns().add(stepRun);
            stepRun.setTeststep(teststep);

            //  test step run starts
            stepRun.setStartTime(new Date());

            //  run test step and get result
            Object result = TeststepRunnerFactory.getInstance()
                    .newTeststepRunner(teststep, teststepDAO, utilsDAO).run();
            LOGGER.info(result == null ? null : result.toString());

            //  get endpoint response
            Object response = null;
            if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
                //  currently assertions in SOAP test step are against the HTTP response body
                response = ((SOAPTeststepRunResult) result).getHttpResponseBody();
            } else if (Teststep.TYPE_MQ.equals(teststep.getType())) {
                response = ((MQTeststepRunResult) result).getValue();
            } else {
                response = result;
            }
            stepRun.setResponse(response);

            //  verify assertions against the endpoint response
            for (Assertion assertion : teststep.getAssertions()) {
                AssertionVerifier verifier = new AssertionVerifierFactory().create(assertion.getType());
                AssertionVerificationResult verificationResult = verifier.verify(assertion, response);
                if (Boolean.FALSE == verificationResult.getPassed() &&
                        !testcaseRun.getFailedTeststepIds().contains(teststep.getId())) {
                    testcaseRun.getFailedTeststepIds().add(teststep.getId());
                }
            }

            //  test step run ends
            stepRun.setDuration(new Date().getTime() - stepRun.getStartTime().getTime());
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());

        testcaseRun.setResult(testcaseRun.getFailedTeststepIds().size() > 0 ? TestResult.FAILED : TestResult.PASSED);

        //  TODO persist testcaseRun into database

        return testcaseRun;
    }
}