package au.com.billon.stt.resources;

import au.com.billon.stt.db.AssertionDAO;
import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.Testcase;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    public Assertion create(@PathParam("teststepId") long teststepId, Assertion assertion) throws JsonProcessingException {
        assertion.setTeststepId(teststepId);
        assertion.serializeProperties();
        long id = dao.insert(assertion);
        assertion.setId(id);
        return assertion;
    }

    @GET
    public List<Assertion> findAll() {
        return dao.findAll();
    }
}
