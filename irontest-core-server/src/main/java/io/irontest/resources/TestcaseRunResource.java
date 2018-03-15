package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.runner.DataDrivenTestcaseRunner;
import io.irontest.core.runner.RegularTestcaseRunner;
import io.irontest.core.runner.TestcaseRunner;
import io.irontest.db.*;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.views.TestcaseRunView;
import io.irontest.views.TeststepRunView;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Trevor Li on 24/07/2015.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private final TestcaseDAO testcaseDAO;
    private final UserDefinedPropertyDAO udpDAO;
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;
    private final TestcaseRunDAO testcaseRunDAO;
    private final TeststepRunDAO teststepRunDAO;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO,
                               UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, TeststepRunDAO teststepRunDAO) {
        this.testcaseDAO = testcaseDAO;
        this.udpDAO = udpDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.teststepRunDAO = teststepRunDAO;
    }

    @POST @Path("testcaseruns")
    @PermitAll
    public TestcaseRun create(@QueryParam("testcaseId") long testcaseId) throws JsonProcessingException {
        List<LinkedHashMap<String, Object>> dataTable = utilsDAO.getTestcaseDataTable(testcaseId);
        TestcaseRunner testcaseRunner;
        if (dataTable.isEmpty()) {
            testcaseRunner = new RegularTestcaseRunner(testcaseId, testcaseDAO, udpDAO, teststepDAO, utilsDAO, testcaseRunDAO);
        } else {
            testcaseRunner = new DataDrivenTestcaseRunner(testcaseId, testcaseDAO, udpDAO, teststepDAO, utilsDAO, testcaseRunDAO);
        }
        return testcaseRunner.run();
    }

    @GET @Path("testcaseruns/{testcaseRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TestcaseRunView getHTMLReportByTestcaseRunId(@PathParam("testcaseRunId") long testcaseRunId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findById(testcaseRunId);
        return new TestcaseRunView(testcaseRun);
    }

    @GET @Path("teststepruns/{teststepRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TeststepRunView getStepRunHTMLReportById(@PathParam("teststepRunId") long teststepRunId) {
        TeststepRun teststepRun = teststepRunDAO.findById(teststepRunId);
        return new TeststepRunView(teststepRun);
    }

    @GET @Path("testcaseruns/lastrun/htmlreport") @Produces(MediaType.TEXT_HTML)
    public Object getTestcaseLastRunHTMLReport(@QueryParam("testcaseId") long testcaseId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findLastByTestcaseId(testcaseId);
        if (testcaseRun == null) {
            return "The test case has never been run.";
        } else {
            return new TestcaseRunView(testcaseRun);
        }
    }
}