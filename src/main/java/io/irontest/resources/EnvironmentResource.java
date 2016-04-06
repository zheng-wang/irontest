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
    private final EnvironmentDAO dao;
    private final EnvEntryDAO entryDao;

    public EnvironmentResource(EnvironmentDAO dao, EnvEntryDAO entryDao) {
        this.dao = dao;
        this.entryDao = entryDao;
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
        List<EnvEntry> entries = environment.getEntries();
        for (EnvEntry entry: entries) {
            if (entry.getId() > 0) {
                entryDao.update(entry);
            } else {
                entry.setEnvironmentId(environment.getId());
                entryDao.insert(entry);
            }
        }
        return findById(environment.getId());
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
        Environment environment = dao.findById(environmentId);
        List<EnvEntry> entries = entryDao.findByEnv(environmentId);
        environment.setEntries(entries);
        return environment;
    }
}
