package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.models.UserDefinedProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 30/08/2017.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class UDPResource {
    private UserDefinedPropertyDAO udpDAO;
    public UDPResource(UserDefinedPropertyDAO udpDAO) {
        this.udpDAO = udpDAO;
    }

    @GET @Path("testcases/{testcaseId}/udps")
    public List<UserDefinedProperty> findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return udpDAO.findByTestcaseId(testcaseId);
    }

    @POST @Path("testcases/{testcaseId}/udps")
    public UserDefinedProperty create(@PathParam("testcaseId") long testcaseId) {
        return udpDAO.insert(testcaseId);
    }

    @PUT @Path("udps/{udpId}")
    public void update(UserDefinedProperty udp) throws JsonProcessingException {
        udpDAO.update(udp);
    }

    @DELETE @Path("udps/{udpId}")
    public void delete(@PathParam("udpId") long udpId) {
        udpDAO.deleteById(udpId);
    }
}
