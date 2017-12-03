package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.EndpointDAO;
import io.irontest.models.Environment;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.SOAPEndpointProperties;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 * Not using @Path("/endpoints") at class level, as this resource has a URI /environments/...
 * JAX-RS resolves URI to root resource class first.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class ManagedEndpointResource {
    private final EndpointDAO endpointDAO;

    public ManagedEndpointResource(EndpointDAO endpointDAO) {
        this.endpointDAO = endpointDAO;
    }

    @POST @Path("environments/{environmentId}/endpoints")
    @PermitAll
    public Endpoint create(@PathParam("environmentId") long environmentId, Endpoint endpoint) throws JsonProcessingException {
        Environment env = new Environment();
        env.setId(environmentId);
        endpoint.setEnvironment(env);
        if (Endpoint.TYPE_SOAP.equals(endpoint.getType())) {
            endpoint.setOtherProperties(new SOAPEndpointProperties());
        }
        long id = endpointDAO.insertManagedEndpoint(endpoint);
        endpoint.setId(id);
        return endpoint;
    }

    @PUT @Path("endpoints/{endpointId}")
    @PermitAll
    public Endpoint update(Endpoint endpoint) throws JsonProcessingException {
        endpointDAO.update(endpoint);
        return endpointDAO.findById(endpoint.getId());
    }

    @DELETE @Path("endpoints/{endpointId}")
    @PermitAll
    public void delete(@PathParam("endpointId") long endpointId) {
        endpointDAO.deleteById(endpointId);
    }

    @GET @Path("endpoints/{endpointId}")
    public Endpoint findById(@PathParam("endpointId") long endpointId) {
        Endpoint endpoint = endpointDAO.findById(endpointId);
        return endpoint;
    }

    @GET @Path("endpoints")
    public List<Endpoint> findByType(@QueryParam("type") String endpointType) {
        return endpointDAO.findManagedEndpointsByType(endpointType);
    }
}
