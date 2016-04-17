package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.*;
import io.irontest.handlers.HandlerFactory;
import io.irontest.models.Endpoint;
import io.irontest.models.Testcase;
import io.irontest.models.Testrun;
import io.irontest.models.Teststep;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.EvaluationResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestrunResource {
    private final EndpointDAO endpointDao;
    private final EndpointDetailDAO endpointdtlDao;
    private final TestcaseDAO testcaseDao;
    private final TeststepDAO teststepDao;
    private final AssertionDAO assertionDao;
    private final UtilsDAO utilsDAO;

    public TestrunResource(EndpointDAO endpointDao, EndpointDetailDAO endpointdtlDao, TestcaseDAO testcaseDao,
                           TeststepDAO teststepDao, AssertionDAO assertionDao, UtilsDAO utilsDAO) {
        this.endpointDao = endpointDao;
        this.endpointdtlDao = endpointdtlDao;
        this.testcaseDao = testcaseDao;
        this.teststepDao = teststepDao;
        this.assertionDao = assertionDao;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getTeststepId() > 0) {  //  run a test step
            Teststep teststep = teststepDao.findById(testrun.getTeststepId());
            Endpoint endpoint = endpointDao.findById(teststep.getEndpoint().getId());
            endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
            Object response = HandlerFactory.getInstance().getHandler(endpoint.getType() + "Handler")
                    .invoke(testrun.getRequest(), endpoint);
            testrun.setResponse(response);
        } else if (testrun.getTestcaseId() > 0) {    //  run a test case
            long testcaseId = testrun.getTestcaseId();
            Testcase testcase = testcaseDao.findById(testcaseId);
            List<Teststep> teststeps = teststepDao.findByTestcaseId(testcaseId);

            for (Teststep teststep : teststeps) {
                //  invoke and get response
                Endpoint endpoint = endpointDao.findById(teststep.getEndpoint().getId());
                endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
                Object response = HandlerFactory.getInstance().getHandler(endpoint.getType() + "Handler")
                        .invoke(teststep.getRequest(), endpoint);

                System.out.println(response);

                //  evaluate assertions against the invocation response
                List<Assertion> assertions = assertionDao.findByTeststepId(teststep.getId());
                EvaluationResult result = new EvaluationResult();
                for (Assertion assertion : assertions) {
                    AssertionVerification verification = new AssertionVerification();
                    verification.setAssertion(assertion);
                    verification.setInput(response);
                    AssertionVerifier verifier = new AssertionVerifierFactory().create(assertion.getType());
                    AssertionVerificationResult verificationResult = verifier.verify(verification);
                    if (Boolean.FALSE == verificationResult.getPassed()) {
                        result.setError("true");
                        break;
                    }
                }
                teststep.setResult(result);
            }

            testcase.setTeststeps(teststeps);
            testrun.setTestcase(testcase);
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
