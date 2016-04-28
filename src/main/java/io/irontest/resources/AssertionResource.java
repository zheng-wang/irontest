package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.AssertionDAO;
import io.irontest.models.assertion.Assertion;

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
        return dao.insert(assertion);
    }

    @PUT @Path("{assertionId}")
    public Assertion update(Assertion assertion) throws JsonProcessingException {
        return dao.update(assertion);
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
