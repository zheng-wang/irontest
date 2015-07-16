package au.com.billon.stt.resources;

import au.com.billon.stt.db.EndpointDAO;
import au.com.billon.stt.db.EndpointDetailDAO;
import au.com.billon.stt.handlers.HandlerFactory;
import au.com.billon.stt.models.Endpoint;
import au.com.billon.stt.models.EndpointDetail;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/endpoints") @Produces({ MediaType.APPLICATION_JSON })
public class EndpointResource {
    private final EndpointDAO dao;
    private final EndpointDetailDAO detailDao;

    public EndpointResource(EndpointDAO dao, EndpointDetailDAO detailDao) {
        this.dao = dao;
        this.detailDao = detailDao;
    }

    @POST
    public Endpoint create(Endpoint endpoint) {
        long id = dao.insert(endpoint);
        endpoint.setId(id);

        List<EndpointDetail> details = endpoint.getDetails();
        for (EndpointDetail detail : details) {
            detail.setEndpointId(endpoint.getId());
            detailDao.insert(detail);
        }

        return endpoint;
    }

    @PUT @Path("{endpointId}")
    public Endpoint update(Endpoint endpoint) {
        dao.update(endpoint);

        List<EndpointDetail> details = endpoint.getDetails();
        for (EndpointDetail detail : details) {
            detailDao.update(detail);
        }

        return dao.findById(endpoint.getId());
    }

    @DELETE @Path("{endpointId}")
    public void delete(@PathParam("endpointId") long endpointId) {
        dao.deleteById(endpointId);
    }

    @GET
    public List<Endpoint> findAll() {
        return dao.findAll();
    }

    @GET @Path("/handler/{handlerName}")
    public List<EndpointDetail> getProperties(@PathParam("handlerName") String handlerName) {
        List<String> properties = HandlerFactory.getInstance().getHandler(handlerName).getProperties();
        List<EndpointDetail> details = new ArrayList<EndpointDetail>();

        for (String property : properties) {
            EndpointDetail detail = new EndpointDetail();
            detail.setName(property);

            details.add(detail);
        }

        return details;
    }

    @GET @Path("{endpointId}")
    public Endpoint findById(@PathParam("endpointId") long endpointId) {
        Endpoint endpoint = dao.findById(endpointId);
        endpoint.setDetails(detailDao.findByEndpoint(endpointId));
        return endpoint;
    }
}
