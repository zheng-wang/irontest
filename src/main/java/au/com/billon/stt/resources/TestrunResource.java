package au.com.billon.stt.resources;

import au.com.billon.stt.core.Evaluator;
import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.db.*;
import au.com.billon.stt.handlers.HandlerFactory;
import au.com.billon.stt.models.*;

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
    private final EnvironmentDAO environmentDao;
    private final EnvEntryDAO enventryDao;
    private final IntfaceDAO intfaceDao;
    private final AssertionDAO assertionDao;
    private final EvaluatorFactory evaluatorFactory;

    public TestrunResource(EndpointDAO endpointDao, EndpointDetailDAO endpointdtlDao, TestcaseDAO testcaseDao,
                           TeststepDAO teststepDao, EnvironmentDAO environmentDao, EnvEntryDAO enventryDao,
                           IntfaceDAO intfaceDao, AssertionDAO assertionDao, EvaluatorFactory evaluatorFactory) {
        this.endpointDao = endpointDao;
        this.endpointdtlDao = endpointdtlDao;
        this.testcaseDao = testcaseDao;
        this.teststepDao = teststepDao;
        this.environmentDao = environmentDao;
        this.enventryDao = enventryDao;
        this.intfaceDao = intfaceDao;
        this.assertionDao = assertionDao;
        this.evaluatorFactory = evaluatorFactory;
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

            Map<String, String> details = convertDetails(endpointdtlDao.findByEndpoint(endpointId));

            Object response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(testrun.getRequest(), details);

            testrun.setEndpoint(endpoint);
            testrun.setResponse(response);
        } else if (testrun.getTestcaseId() > 0) {
            long testcaseId = testrun.getTestcaseId();
            Testcase testcase = testcaseDao.findById(testcaseId);
            List<Teststep> teststeps = teststepDao.findByTestcaseId(testcaseId);

            long environmentId = testrun.getEnvironmentId();
            Environment environment = environmentDao.findById(environmentId);
            List<EnvEntry> enventries = enventryDao.findByEnv(environmentId);
            Map<Long, EnvEntry> enventriesMap = new HashMap<Long, EnvEntry>();
            for (EnvEntry enventry : enventries) {
                enventriesMap.put(enventry.getIntfaceId(), enventry);
            }

            // set the default environment for the test case
            testcase.setEnvironmentId(environmentId);
            testcaseDao.update(testcase);

            for (Teststep teststep : teststeps) {
                long intfaceId = teststep.getIntfaceId();
                EnvEntry enventry = enventriesMap.get(intfaceId);
                if (enventry == null) {
                    throw new Exception("No interface entry for the test step " + teststep.getName() + " in the environment " + environment.getName());
                } else {
                    long endpointId = enventry.getEndpointId();
                    Endpoint endpoint = endpointDao.findById(endpointId);
                    Map<String, String> details = convertDetails(endpointdtlDao.findByEndpoint(endpointId));
                    Object response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(teststep.getRequest(), details);

                    System.out.println(response);

                    Intface intface = intfaceDao.findById(intfaceId);
                    List<Assertion> assertions = assertionDao.findByTeststepId(teststep.getId());

                    EvaluationResponse result = new EvaluationResponse();
                    for (Assertion assertion : assertions) {
                        Evaluator evaluator = evaluatorFactory.createEvaluator(intface.getDeftype(), assertion.getType());
                        result = evaluator.evaluate(response, assertion.getProperties());
                        if (result.isError()) {
                            break;
                        }
                    }

                    teststep.setResult(result);
                }
            }

            testrun.setEnvironment(environment);
            testcase.setTeststeps(teststeps);
            testrun.setTestcase(testcase);
        }

        return testrun;
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
