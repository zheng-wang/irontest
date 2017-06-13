package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.EndpointDAO;
import io.irontest.models.endpoint.Endpoint;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
        long id = endpointDAO.insertManagedEndpoint(endpoint);
        endpoint.setId(id);
        return endpoint;
    }

    @PUT @Path("{endpointId}")
    public Endpoint update(Endpoint endpoint) throws JsonProcessingException {
        endpointDAO.update(endpoint);
        return endpointDAO.findById(endpoint.getId());
    }

    @DELETE @Path("{endpointId}")
    public void delete(@PathParam("endpointId") long endpointId) {
        endpointDAO.deleteById(endpointId);
    }

    @GET @Path("{endpointId}")
    public Endpoint findById(@PathParam("endpointId") long endpointId) {
        Endpoint endpoint = endpointDAO.findById(endpointId);
        return endpoint;
    }
}
