package io.irontest.resources;

import io.irontest.db.EnvironmentDAO;
import io.irontest.models.Environment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/environments") @Produces({ MediaType.APPLICATION_JSON })
public class EnvironmentResource {
    private final EnvironmentDAO environmentDAO;

    public EnvironmentResource(EnvironmentDAO environmentDAO) {
        this.environmentDAO = environmentDAO;
    }

    @POST
    public Environment create() {
        Environment result = new Environment();
        result.setId(environmentDAO.insert());
        return result;
    }

    @PUT @Path("{environmentId}")
    public Environment update(Environment environment) {
        environmentDAO.update(environment);
        return environmentDAO.findById(environment.getId());
    }

    @DELETE @Path("{environmentId}")
    public void delete(@PathParam("environmentId") long environmentId) {
        environmentDAO.deleteById(environmentId);
    }

    @GET
    public List<Environment> findAll() {
        return environmentDAO.findAll();
    }

    @GET @Path("{environmentId}")
    public Environment findById(@PathParam("environmentId") long environmentId) {
        return environmentDAO.findById(environmentId);
    }
}
