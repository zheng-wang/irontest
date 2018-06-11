package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.core.runner.DataDrivenTestcaseRunner;
import io.irontest.core.runner.RegularTestcaseRunner;
import io.irontest.core.runner.TestcaseRunner;
import io.irontest.db.*;
import io.irontest.models.DataTable;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.views.TestcaseRunView;
import io.irontest.views.TeststepRunView;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private final TestcaseDAO testcaseDAO;
    private final UserDefinedPropertyDAO udpDAO;
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;
    private final DataTableDAO dataTableDAO;
    private final TestcaseRunDAO testcaseRunDAO;
    private final TeststepRunDAO teststepRunDAO;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO,
                               UtilsDAO utilsDAO, DataTableDAO dataTableDAO, TestcaseRunDAO testcaseRunDAO, TeststepRunDAO teststepRunDAO) {
        this.testcaseDAO = testcaseDAO;
        this.udpDAO = udpDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
        this.dataTableDAO = dataTableDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.teststepRunDAO = teststepRunDAO;
    }

    @POST @Path("testcaseruns") @PermitAll
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    public TestcaseRun create(@QueryParam("testcaseId") long testcaseId) throws JsonProcessingException {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseId);
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findByTestcaseId(testcaseId);
        DataTable dataTable = dataTableDAO.getTestcaseDataTable(testcaseId, false);
        TestcaseRunner testcaseRunner;
        if (dataTable.getRows().isEmpty()) {
            testcaseRunner = new RegularTestcaseRunner(testcase, testcaseUDPs, teststepDAO, utilsDAO, testcaseRunDAO);
        } else {
            testcaseRunner = new DataDrivenTestcaseRunner(testcase, testcaseUDPs, dataTable, teststepDAO, utilsDAO,
                    testcaseRunDAO);
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