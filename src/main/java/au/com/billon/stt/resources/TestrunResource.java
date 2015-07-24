package au.com.billon.stt.resources;

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
    private final EndpointDetailDAO detailDao;

    public TestrunResource(EndpointDetailDAO detailDao) {
        this.detailDao = detailDao;
    }

    @POST
    public Testrun create(Testrun testrun) throws Exception {
        if (testrun.getDetails() != null) {
            Map<String, String> details = testrun.getDetails();
            details.put("url", details.get("wsdlUrl"));
            String response = HandlerFactory.getInstance().getHandler("SOAPHandler").invoke(testrun.getRequest(), testrun.getDetails());
            testrun.setResponse(response);
        } else if (testrun.getEndpoint() != null) {
            Endpoint endpoint = testrun.getEndpoint();
            List<EndpointDetail> detailsArray = detailDao.findByEndpoint(endpoint.getId());
            endpoint.setDetails(detailsArray);

            Map<String, String> details = new HashMap<String, String>();
            for (EndpointDetail detail : detailsArray) {
                details.put(detail.getName(), detail.getValue());
            }

            String response = HandlerFactory.getInstance().getHandler("SOAPHandler").invoke(testrun.getRequest(), details);
            testrun.setResponse(response);
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
