package au.com.billon.stt.resources;

import au.com.billon.stt.db.EndpointDAO;
import au.com.billon.stt.db.EndpointDetailDAO;
import au.com.billon.stt.handlers.HandlerFactory;
import au.com.billon.stt.models.Endpoint;
import au.com.billon.stt.models.EndpointDetail;
import au.com.billon.stt.models.Testrun;

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

    public TestrunResource(EndpointDAO endpointDao, EndpointDetailDAO endpointdtlDao) {
        this.endpointDao = endpointDao;
        this.endpointdtlDao = endpointdtlDao;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getDetails() != null) {
            Map<String, String> details = testrun.getDetails();
            details.put("url", details.get("wsdlUrl"));
            Object response = HandlerFactory.getInstance().getHandler("SOAPHandler").invoke(testrun.getRequest(), testrun.getDetails());
            testrun.setResponse(response);
        } else if (testrun.getEndpointId() > 0) {
            long endpointId = testrun.getEndpointId();
            Endpoint endpoint = endpointDao.findById(endpointId);
            testrun.setEndpoint(endpoint);

            List<EndpointDetail> detailsArray = endpointdtlDao.findByEndpoint(endpointId);

            Map<String, String> details = new HashMap<String, String>();
            for (EndpointDetail detail : detailsArray) {
                details.put(detail.getName(), detail.getValue());
            }

            Object response = HandlerFactory.getInstance().getHandler(endpoint.getHandler()).invoke(testrun.getRequest(), details);
            testrun.setResponse(response);
        } else if (testrun.getTestcaseId() > 0) {

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
