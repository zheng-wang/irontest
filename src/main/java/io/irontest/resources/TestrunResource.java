package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.AssertionDAO;
import io.irontest.db.EndpointDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.handlers.HandlerFactory;
import io.irontest.models.Endpoint;
import io.irontest.models.Testrun;
import io.irontest.models.Teststep;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/testruns") @Produces({ MediaType.APPLICATION_JSON })
public class TestrunResource {
    private final EndpointDAO endpointDao;
    private final TeststepDAO teststepDao;
    private final AssertionDAO assertionDao;
    private final UtilsDAO utilsDAO;

    public TestrunResource(EndpointDAO endpointDao, TeststepDAO teststepDao, AssertionDAO assertionDao,
                           UtilsDAO utilsDAO) {
        this.endpointDao = endpointDao;
        this.teststepDao = teststepDao;
        this.assertionDao = assertionDao;
        this.utilsDAO = utilsDAO;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getTeststepId() != null) {  //  run a test step (passing invocation response back to client)
            Teststep teststep = teststepDao.findById(testrun.getTeststepId());
            Endpoint endpoint = endpointDao.findById(teststep.getEndpoint().getId());
            endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
            Object response = HandlerFactory.getInstance().getHandler(endpoint.getType() + "Handler")
                    .invoke(testrun.getRequest(), endpoint);
            testrun.setResponse(response);
        } else if (testrun.getTestcaseId() != null) {  //  run a test case (not passing invocation responses back to client)
            long testcaseId = testrun.getTestcaseId();
            List<Teststep> teststeps = teststepDao.findByTestcaseId(testcaseId);

            for (Teststep teststep : teststeps) {
                //  invoke and get response
                Endpoint endpoint = endpointDao.findById(teststep.getEndpoint().getId());
                endpoint.setPassword(utilsDAO.decryptPassword(endpoint.getPassword()));
                Object response = HandlerFactory.getInstance().getHandler(endpoint.getType() + "Handler")
                        .invoke(teststep.getRequest(), endpoint);

                System.out.println(response);

                //  verify assertions against the invocation response
                List<Assertion> assertions = assertionDao.findByTeststepId(teststep.getId());
                for (Assertion assertion : assertions) {
                    AssertionVerification verification = new AssertionVerification();
                    verification.setAssertion(assertion);
                    verification.setInput(response);
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
