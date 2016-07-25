package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.runner.MQTeststepRunResult;
import io.irontest.core.runner.SOAPTeststepRunResult;
import io.irontest.core.runner.TeststepRunnerFactory;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.TestcaseRun;
import io.irontest.models.Teststep;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testcaseruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestcaseRunResource.class);
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;

    public TestcaseRunResource(TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public TestcaseRun create(TestcaseRun testcaseRun) throws Exception {
        List<Teststep> teststeps = teststepDAO.findByTestcaseId(testcaseRun.getTestcaseId());

        for (Teststep teststep : teststeps) {
            //  run and get result
            Object result = TeststepRunnerFactory.getInstance()
                    .newTeststepRunner(teststep, teststepDAO, utilsDAO).run();
            LOGGER.info(result == null ? null : result.toString());

            //  verify assertions against the invocation response
            for (Assertion assertion : teststep.getAssertions()) {
                Object input = null;
                if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
                    //  currently assertions in SOAP test step are against the HTTP response body
                    input = ((SOAPTeststepRunResult) result).getHttpResponseBody();
                } else if (Teststep.TYPE_MQ.equals(teststep.getType())) {
                    input = ((MQTeststepRunResult) result).getValue();
                } else {
                    input = result;
                }
                AssertionVerifier verifier = new AssertionVerifierFactory().create(assertion.getType());
                AssertionVerificationResult verificationResult = verifier.verify(assertion, input);
                if (Boolean.FALSE == verificationResult.getPassed()) {
                    testcaseRun.getFailedTeststepIds().add(teststep.getId());
                    break;
                }
            }
        }

        return testcaseRun;
    }

    @DELETE @Path("{testrunId}")
    public void delete(@PathParam("testrunId") long testrunId) {
    }

    @GET
    public List<TestcaseRun> findAll() {
        return null;
    }

    @GET @Path("{testrunId}")
    public TestcaseRun findById(@PathParam("testrunId") long testrunId) {
        return null;
    }
}