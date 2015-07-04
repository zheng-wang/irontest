package au.com.billon.stt.resources;

import au.com.billon.stt.db.TestcaseDAO;
import au.com.billon.stt.models.Testcase;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
@Path("/testcases") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseResource {
    private final TestcaseDAO dao;

    public TestcaseResource(TestcaseDAO dao) {
        this.dao = dao;
    }

    @POST
    public Testcase create(Testcase testcase) {
        long id = dao.insert(testcase);
        testcase.setId(id);
        return testcase;
    }

    @PUT @Path("{testcaseId}")
    public Testcase update(Testcase testcase) {
        dao.update(testcase);
        return dao.findById(testcase.getId());
    }

    @DELETE @Path("{testcaseId}")
    public void delete(@PathParam("testcaseId") long testcaseId) {
        dao.deleteById(testcaseId);
    }

    @GET
    public List<Testcase> findAll() {
        return dao.findAll();
    }

    @GET @Path("{testcaseId}")
    public Testcase findById(@PathParam("testcaseId") long testcaseId) {
        return dao.findById(testcaseId);
    }
}
