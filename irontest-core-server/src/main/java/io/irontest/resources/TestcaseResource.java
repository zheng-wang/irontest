package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jersey.PATCH;
import io.irontest.db.TestcaseDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.models.Testcase;
import io.irontest.models.teststep.Teststep;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseResource {
    private final TestcaseDAO testcaseDAO;
    private final TeststepDAO teststepDAO;

    public TestcaseResource(TestcaseDAO testcaseDAO, TeststepDAO teststepDAO) {
        this.testcaseDAO = testcaseDAO;
        this.teststepDAO = teststepDAO;
    }

    @PUT @Path("testcases/{testcaseId}")
    @PermitAll
    public Testcase update_TestcaseEditView(Testcase testcase) {
        testcaseDAO.update(testcase);
        return testcaseDAO.findById_TestcaseEditView(testcase.getId());
    }

    /*@DELETE @Path("{testcaseId}")
    @PermitAll
    public void delete(@PathParam("testcaseId") long testcaseId) {
        testcaseDAO.deleteById(testcaseId);
    }*/

    @GET @Path("testcases/{testcaseId}")
    public Testcase findById_TestcaseEditView(@PathParam("testcaseId") long testcaseId) {
        return testcaseDAO.findById_TestcaseEditView(testcaseId);
    }

    @PATCH @Path("testcases/{testcaseId}/moveStep")
    @PermitAll
    public Testcase moveStep(Testcase testcase) throws JsonProcessingException {
        List<Teststep> teststeps = testcase.getTeststeps();
        teststepDAO.moveInTestcase(testcase.getId(), teststeps.get(0).getSequence(), teststeps.get(1).getSequence());
        return testcaseDAO.findById_TestcaseEditView(testcase.getId());
    }

    /**
     * @param testcaseId
     * @param targetFolderId
     * @return the new test case (containing ID only)
     */
    @POST @Path("testcases/{testcaseId}/duplicate")
    @PermitAll
    public Testcase duplicate(@PathParam("testcaseId") long testcaseId,
                          @QueryParam("targetFolderId") long targetFolderId) throws JsonProcessingException {
        Testcase testcase = new Testcase();
        testcase.setId(testcaseDAO.duplicate(testcaseId, targetFolderId));
        return testcase;
    }
}
