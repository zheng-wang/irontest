package au.com.billon.stt.resources;

import au.com.billon.stt.db.AssertionDAO;
import au.com.billon.stt.models.Assertion;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public Assertion create(@PathParam("teststepId") long teststepId, Assertion assertion) {
        assertion.setTeststepId(teststepId);
        long id = dao.insert(assertion);
        assertion.setId(id);
        return assertion;
    }
}
