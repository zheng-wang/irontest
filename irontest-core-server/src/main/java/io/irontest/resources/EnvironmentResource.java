package io.irontest.resources;

import io.irontest.db.EnvironmentDAO;
import io.irontest.models.Environment;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Not using @Path("/environments") at class level, as the {@link ManagedEndpointResource} has a URI /environments/...
 * JAX-RS resolves URI to root resource class first.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class EnvironmentResource {
    private final EnvironmentDAO environmentDAO;

    public EnvironmentResource(EnvironmentDAO environmentDAO) {
        this.environmentDAO = environmentDAO;
    }

    @POST @Path("environments")
    @PermitAll
    public Environment create() {
        Environment result = new Environment();
        result.setId(environmentDAO.insert());
        return result;
    }

    @PUT @Path("environments/{environmentId}")
    @PermitAll
    public Environment update_EnvironmentEditView(Environment environment) {
        environmentDAO.update(environment);
        return environmentDAO.findById_EnvironmentEditView(environment.getId());
    }

    @DELETE @Path("environments/{environmentId}")
    @PermitAll
    public void delete(@PathParam("environmentId") long environmentId) {
        environmentDAO.deleteById(environmentId);
    }

    @GET @Path("environments")
    public List<Environment> findAll() {
        return environmentDAO.findAll();
    }

    @GET @Path("environments/{environmentId}")
    public Environment findById_EnvironmentEditView(@PathParam("environmentId") long environmentId) {
        return environmentDAO.findById_EnvironmentEditView(environmentId);
    }
}
