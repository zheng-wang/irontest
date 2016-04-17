package io.irontest.resources;

import io.irontest.db.TestcaseDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.models.Testcase;
import io.irontest.models.Teststep;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@Path("/testcases") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseResource {
    private final TestcaseDAO testcaseDAO;
    private final TeststepDAO teststepDAO;

    public TestcaseResource(TestcaseDAO testcaseDAO, TeststepDAO teststepDAO) {
        this.testcaseDAO = testcaseDAO;
        this.teststepDAO = teststepDAO;
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
        Testcase result = testcaseDAO.findById(testcaseId);
        List<Teststep> teststeps = teststepDAO.findByTestcaseId_PrimaryProperties(testcaseId);
        result.setTeststeps(teststeps);
        return result;
    }
}
