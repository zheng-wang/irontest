package io.irontest.resources;

import io.irontest.db.EnvEntryDAO;
import io.irontest.models.EnvEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/enventries") @Produces({ MediaType.APPLICATION_JSON })
public class EnvEntryResource {
    private final EnvEntryDAO dao;

    public EnvEntryResource(EnvEntryDAO dao) {
        this.dao = dao;
    }

    @POST
    public EnvEntry create(EnvEntry enventry) {
        long id = dao.insert(enventry);
        enventry.setId(id);
        return enventry;
    }

    @PUT @Path("{enventryId}")
    public EnvEntry update(EnvEntry enventry) {
        dao.update(enventry);
        return dao.findById(enventry.getId());
    }

    @DELETE @Path("{enventryId}")
    public void delete(@PathParam("enventryId") long enventryId) {
        dao.deleteById(enventryId);
    }

    @GET @Path("{enventryId}")
    public EnvEntry findById(@PathParam("enventryId") long enventryId) {
        return dao.findById(enventryId);
    }

    @GET @Path("/env/{environmentId}")
    public List<EnvEntry> findByEnv(@PathParam("environmentId") long environmentId) { return dao.findByEnv(environmentId); }
}
