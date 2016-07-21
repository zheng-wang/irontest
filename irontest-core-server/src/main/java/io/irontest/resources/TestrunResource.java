package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.runner.SOAPTeststepRunResult;
import io.irontest.core.runner.TeststepRunnerFactory;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.Testrun;
import io.irontest.models.Teststep;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestrunResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestrunResource.class);
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;

    public TestrunResource(TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getTestcaseId() != null) {  //  run a test case (not passing invocation responses back to client)
            List<Teststep> teststeps = teststepDAO.findByTestcaseId(testrun.getTestcaseId());

            for (Teststep teststep : teststeps) {
                //  run and get result
                Object result = TeststepRunnerFactory.getInstance()
                        .newTeststepRunner(teststep, teststepDAO, utilsDAO).run();
                LOGGER.info(result == null ? null : result.toString());

                //  verify assertions against the invocation response
                for (Assertion assertion : teststep.getAssertions()) {
                    AssertionVerification verification = new AssertionVerification();
                    verification.setAssertion(assertion);
                    if (Teststep.TYPE_SOAP.equals(teststep.getType())) {
                        //  currently assertions in SOAP test step are against the HTTP response body
                        verification.setInput(((SOAPTeststepRunResult) result).getHttpResponseBody());
                    } else {
                        verification.setInput(result);
                    }
                    AssertionVerifier verifier = new AssertionVerifierFactory().create(assertion.getType());
                    AssertionVerificationResult verificationResult = verifier.verify(verification);
                    if (Boolean.FALSE == verificationResult.getPassed()) {
                        testrun.getFailedTeststepIds().add(teststep.getId());
                        break;
                    }
                }
            }
        }

        return testrun;
    }

    @DELETE @Path("{testrunId}")
    public void delete(@PathParam("testrunId") long testrunId) {
    }

    @GET
    public List<Testrun> findAll() {
        return null;
    }

    @GET @Path("{testrunId}")
    public Testrun findById(@PathParam("testrunId") long testrunId) {
        return null;
    }
}