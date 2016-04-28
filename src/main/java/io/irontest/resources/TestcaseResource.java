package io.irontest.resources;

import io.irontest.db.TestcaseDAO;
import io.irontest.models.Testcase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@Path("/testcases") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseResource {
    private final TestcaseDAO testcaseDAO;

    public TestcaseResource(TestcaseDAO testcaseDAO) {
        this.testcaseDAO = testcaseDAO;
    }

    @POST
    public Testcase create(Testcase testcase) {
        long id = testcaseDAO.insert(testcase);
        testcase.setId(id);
        return testcase;
    }

    @PUT @Path("{testcaseId}")
    public Testcase update(Testcase testcase) {
        testcaseDAO.update(testcase);
        return testcaseDAO.findById(testcase.getId());
    }

    @DELETE @Path("{testcaseId}")
    public void delete(@PathParam("testcaseId") long testcaseId) {
        testcaseDAO.deleteById(testcaseId);
    }

    @GET
    public List<Testcase> findAll() {
        return testcaseDAO.findAll();
    }

    @GET @Path("{testcaseId}")
    public Testcase findById(@PathParam("testcaseId") long testcaseId) {
        return testcaseDAO.findById(testcaseId);
    }
}
