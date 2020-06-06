package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.core.testcase.DataDrivenTestcaseRunner;
import io.irontest.core.testcase.RegularTestcaseRunner;
import io.irontest.core.testcase.TestcaseRunner;
import io.irontest.db.TestcaseDAO;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepRunDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.Testcase;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.views.TestcaseRunView;
import io.irontest.views.TeststepRunView;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private final TestcaseDAO testcaseDAO;
    private final UtilsDAO utilsDAO;
    private final TestcaseRunDAO testcaseRunDAO;
    private final TeststepRunDAO teststepRunDAO;
    private WireMockServer wireMockServer;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO,
                               TeststepRunDAO teststepRunDAO, WireMockServer wireMockServer) {
        this.testcaseDAO = testcaseDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.teststepRunDAO = teststepRunDAO;
        this.wireMockServer = wireMockServer;
    }

    @POST @Path("testcaseruns") @PermitAll
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    public TestcaseRun create(@QueryParam("testcaseId") long testcaseId) throws IOException {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseId);
        TestcaseRunner testcaseRunner;
        if (testcase.getDataTable().getRows().isEmpty()) {
            testcaseRunner = new RegularTestcaseRunner(testcase, utilsDAO, testcaseRunDAO, wireMockServer);
        } else {
            testcaseRunner = new DataDrivenTestcaseRunner(testcase, utilsDAO, testcaseRunDAO, wireMockServer);
        }
        return testcaseRunner.run();
    }

    @GET @Path("testcaseruns/{testcaseRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TestcaseRunView getHTMLReportByTestcaseRunId(@PathParam("testcaseRunId") long testcaseRunId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findById(testcaseRunId);
        return new TestcaseRunView(testcaseRun);
    }

    @GET @Path("teststepruns/{stepRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TeststepRunView getStepRunHTMLReportById(@PathParam("stepRunId") long stepRunId) {
        TeststepRun sstepRun = teststepRunDAO.findById(stepRunId);
        return new TeststepRunView(sstepRun);
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