package au.com.billon.stt.resources;

import au.com.billon.stt.db.IntfaceDAO;
import au.com.billon.stt.models.Intface;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Trevor Li on 7/4/15.
 */
@Path("/intfaces") @Produces({ MediaType.APPLICATION_JSON })
public class IntfaceResource {
    private final IntfaceDAO dao;

    public IntfaceResource(IntfaceDAO dao) {
        this.dao = dao;
    }

    @POST
    public Intface create(Intface intface) {
        long id = dao.insert(intface);
        intface.setId(id);
        return intface;
    }

    @PUT @Path("{intfaceId}")
    public Intface update(Intface intface) {
        dao.update(intface);
        return dao.findById(intface.getId());
    }

    @DELETE @Path("{intfaceId}")
    public void delete(@PathParam("intfaceId") long intfaceId) {
        dao.deleteById(intfaceId);
    }

    @GET
    public List<Intface> findAll() {
        return dao.findAll();
    }

    @GET @Path("{intfaceId}")
    public Intface findById(@PathParam("intfaceId") long intfaceId) {
        return dao.findById(intfaceId);
    }
}
