package io.irontest.resources;

import io.irontest.db.EndpointDAO;
import io.irontest.handlers.HandlerFactory;
import io.irontest.models.Endpoint;
import io.irontest.models.EndpointDetail;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/environments/{environmentId}/endpoints") @Produces({ MediaType.APPLICATION_JSON })
public class EndpointResource {
    private final EndpointDAO endpointDAO;

    public EndpointResource(EndpointDAO endpointDAO) {
        this.endpointDAO = endpointDAO;
    }

    @POST
    public Endpoint create(Endpoint endpoint) {
        long id = endpointDAO.insert(endpoint);
        endpoint.setId(id);
        return endpoint;
    }

    @PUT @Path("{endpointId}")
    public Endpoint update(Endpoint endpoint) {
        endpointDAO.update(endpoint);
        return endpointDAO.findById(endpoint.getId());
    }

    @DELETE @Path("{endpointId}")
    public void delete(@PathParam("endpointId") long endpointId) {
        endpointDAO.deleteById(endpointId);
    }

    @GET
    public List<Endpoint> findAll() {
        return endpointDAO.findAll();
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
        Endpoint endpoint = endpointDAO.findById(endpointId);
        return endpoint;
    }
}
