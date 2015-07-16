package au.com.billon.stt.resources;

import au.com.billon.stt.db.EnvEntryDAO;
import au.com.billon.stt.db.EnvironmentDAO;
import au.com.billon.stt.models.Environment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/environments") @Produces({ MediaType.APPLICATION_JSON })
public class EnvironmentResource {
    private final EnvironmentDAO dao;

    public EnvironmentResource(EnvironmentDAO dao) {
        this.dao = dao;
    }

    @POST
    public Environment create(Environment environment) {
        long id = dao.insert(environment);
        environment.setId(id);
        return environment;
    }

    @PUT @Path("{environmentId}")
    public Environment update(Environment environment) {
        dao.update(environment);
        return dao.findById(environment.getId());
    }

    @DELETE @Path("{environmentId}")
    public void delete(@PathParam("environmentId") long environmentId) {
        dao.deleteById(environmentId);
    }

    @GET
    public List<Environment> findAll() {
        return dao.findAll();
    }

    @GET @Path("{environmentId}")
    public Environment findById(@PathParam("environmentId") long environmentId) {
        return dao.findById(environmentId);
    }
}
