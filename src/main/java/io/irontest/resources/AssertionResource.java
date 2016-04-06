package io.irontest.resources;

import io.irontest.db.AssertionDAO;
import io.irontest.models.Assertion;
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
    public Assertion create(Assertion assertion) throws JsonProcessingException {
        long id = dao.insert(assertion);
        return dao.findById(id);
    }

    @PUT @Path("{assertionId}")
    public Assertion update(Assertion assertion) throws JsonProcessingException {
        dao.update(assertion);
        //  don't return dao.findById(assertion.getId()),
        //  so that some temp state from the client such as the verificationPassed can be returned to the client,
        //  hence not losing state on UI
        return assertion;
    }

    @GET
    public List<Assertion> findAll(@PathParam("teststepId") long teststepId) {
        return dao.findByTeststepId(teststepId);
    }

    @DELETE @Path("{assertionId}")
    public void delete(@PathParam("assertionId") long assertionId) {
        dao.deleteById(assertionId);
    }
}
