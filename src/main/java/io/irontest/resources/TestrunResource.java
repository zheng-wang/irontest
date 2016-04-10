package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.*;
import io.irontest.handlers.HandlerFactory;
import io.irontest.handlers.SOAPHandler;
import io.irontest.models.*;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.EvaluationResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public TestrunResource(EndpointDAO endpointDao, EndpointDetailDAO endpointdtlDao, TestcaseDAO testcaseDao,
                           TeststepDAO teststepDao, AssertionDAO assertionDao) {
        this.endpointDao = endpointDao;
        this.endpointdtlDao = endpointdtlDao;
        this.testcaseDao = testcaseDao;
        this.teststepDao = teststepDao;
        this.assertionDao = assertionDao;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getDetails() != null) {
            Map<String, String> details = testrun.getDetails();
            details.put("url", details.get("soapAddress"));
            Object response = HandlerFactory.getInstance().getHandler("SOAPHandler").invoke(testrun.getRequest(), testrun.getDetails());
            testrun.setResponse(response);
        } else if (testrun.getEndpointId() > 0) {
            long endpointId = testrun.getEndpointId();
            Endpoint endpoint = endpointDao.findById(endpointId);

            Map<String, String> details = getEndpointDetails(endpointId);

            Object response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(testrun.getRequest(), details);

            testrun.setEndpoint(endpoint);
            testrun.setResponse(response);
        } else if (testrun.getTestcaseId() > 0) {
            long testcaseId = testrun.getTestcaseId();
            Testcase testcase = testcaseDao.findById(testcaseId);
            List<Teststep> teststeps = teststepDao.findByTestcaseId(testcaseId);

            for (Teststep teststep : teststeps) {
                Object response = null;

                //  invoke and get response
                if (teststep.getEndpointId() == 0) {  //  there is no endpoint associated with the test step
                    if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
                        SOAPTeststepProperties properties = (SOAPTeststepProperties) teststep.getProperties();
                        SOAPHandler handler = (SOAPHandler) HandlerFactory.getInstance().getHandler("SOAPHandler");
                        response = handler.invoke(teststep.getRequest(), properties);
                    }
                } else {              //  use the endpoint to invoke
                    Endpoint endpoint = endpointDao.findById(teststep.getEndpointId());
                    Map<String, String> details = getEndpointDetails(teststep.getEndpointId());
                    response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(teststep.getRequest(), details);
                }

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

    private Map<String, String> getEndpointDetails(long endpointId) {
        Map<String, String> details = convertDetails(endpointdtlDao.findByEndpoint(endpointId));
        EndpointDetail detailPassword = endpointdtlDao.findByEndpointPassword(endpointId);
        if (detailPassword != null) {
            details.put(detailPassword.getName(), detailPassword.getValue());
        }

        return details;
    }

    private Map<String, String> convertDetails(List<EndpointDetail> detailsArray) {
        Map<String, String> details = new HashMap<String, String>();
        for (EndpointDetail detail : detailsArray) {
            details.put(detail.getName(), detail.getValue());
        }

        return details;
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
