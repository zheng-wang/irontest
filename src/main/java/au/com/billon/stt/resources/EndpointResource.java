package au.com.billon.stt.resources;

import au.com.billon.stt.db.EndpointDAO;
import au.com.billon.stt.models.Endpoint;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/endpoints") @Produces({ MediaType.APPLICATION_JSON })
public class EndpointResource {
    private final EndpointDAO dao;

    public EndpointResource(EndpointDAO dao) {
        this.dao = dao;
    }

    @POST
    public Endpoint create(Endpoint endpoint) {
        long id = dao.insert(endpoint);
        endpoint.setId(id);
        return endpoint;
    }

    @PUT @Path("{endpointId}")
    public Endpoint update(Endpoint endpoint) {
        dao.update(endpoint);
        return endpoint;
    }

    @DELETE @Path("{endpointId}")
    public void delete(@PathParam("endpointId") long endpointId) {
        dao.deleteById(endpointId);
    }

    @GET
    public List<Endpoint> findAll() {
        return dao.findAll();
    }

    @GET @Path("{endpointId}")
    public Endpoint findById(@PathParam("endpointId") long endpointId) {
        return dao.findById(endpointId);
    }
}
