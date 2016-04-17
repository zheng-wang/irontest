package io.irontest.resources;

import io.irontest.db.EnvEntryDAO;
import io.irontest.db.EnvironmentDAO;
import io.irontest.models.EnvEntry;
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
    private final EnvEntryDAO entryDao;

    public EnvironmentResource(EnvironmentDAO environmentDAO, EnvEntryDAO entryDao) {
        this.environmentDAO = environmentDAO;
        this.entryDao = entryDao;
    }

    @POST
    public Environment create(Environment environment) {
        long id = environmentDAO.insert(environment);
        environment.setId(id);
        return environment;
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
        Environment environment = environmentDAO.findById(environmentId);
        List<EnvEntry> entries = entryDao.findByEnv(environmentId);
        environment.setEntries(entries);
        return environment;
    }
}
