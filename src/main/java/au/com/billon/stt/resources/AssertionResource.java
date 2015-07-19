package au.com.billon.stt.resources;

import au.com.billon.stt.db.AssertionDAO;
import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.Testcase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 19/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps/{teststepId}/assertions") @Produces({ MediaType.APPLICATION_JSON })
public class AssertionResource {
    private final AssertionDAO dao;

    public AssertionResource(AssertionDAO dao) {
        this.dao = dao;
    }

    @POST
    public Assertion create(@PathParam("teststepId") long teststepId, Assertion assertion)
            throws JsonProcessingException {
        long id = dao.insert(teststepId, assertion.getName(), assertion.getType(),
                new ObjectMapper().writeValueAsString(assertion.getProperties()));
        return dao.findById(id);
    }

    @PUT @Path("{assertionId}")
    public Assertion update(Assertion assertion) throws JsonProcessingException {
        dao.update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getProperties()),
                assertion.getId());
        return dao.findById(assertion.getId());
    }

    @GET
    public List<Assertion> findAll() {
        return dao.findAll();
    }
}
