package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.EndpointDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.SOAPEndpointProperties;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/endpoints/managed") @Produces({ MediaType.APPLICATION_JSON })
public class ManagedEndpointResource {
    private final EndpointDAO endpointDAO;

    public ManagedEndpointResource(EndpointDAO endpointDAO) {
        this.endpointDAO = endpointDAO;
    }

    @POST
    public Endpoint create(Endpoint endpoint) throws JsonProcessingException {
        if (Endpoint.TYPE_SOAP.equals(endpoint.getType())) {
            endpoint.setOtherProperties(new SOAPEndpointProperties());
        }
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

    @GET
    public List<Endpoint> findByType(@QueryParam("type") String endpointType) {
        return endpointDAO.findManagedEndpointsByType(endpointType);
    }
}
