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

    @PUT @Path("{testcaseId}")
    public Testcase update(Testcase testcase, @QueryParam("moveStep") boolean moveStep) {
        if (moveStep) {  //  move teststep in testcase
            List<Teststep> teststeps = testcase.getTeststeps();
            teststepDAO.moveInTestcase(testcase.getId(), teststeps.get(0).getSequence(), teststeps.get(1).getSequence());
        } else {         //  update testcase details
            testcaseDAO.update(testcase);
        }
        return testcaseDAO.findById_Mini(testcase.getId());
    }

    /*@DELETE @Path("{testcaseId}")
    public void delete(@PathParam("testcaseId") long testcaseId) {
        testcaseDAO.deleteById(testcaseId);
    }*/

    /*@GET
    public List<Testcase> findAll() {
        return testcaseDAO.findAll();
    }*/

    @GET @Path("{testcaseId}")
    public Testcase findById(@PathParam("testcaseId") long testcaseId) {
        return testcaseDAO.findById_Mini(testcaseId);
    }

    /**
     * @param testcaseId
     * @param targetFolderId
     * @return the new test case
     */
    @PUT @Path("{testcaseId}/duplicate")
    public Testcase duplicate(@PathParam("testcaseId") long testcaseId,
                          @QueryParam("targetFolderId") long targetFolderId) {
        Testcase testcase = new Testcase();
        testcase.setId(testcaseDAO.duplicate(testcaseId, targetFolderId));
        return testcase;
    }
}
