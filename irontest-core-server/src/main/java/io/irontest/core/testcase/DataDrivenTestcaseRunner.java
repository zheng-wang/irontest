package io.irontest.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.rits.cloning.Cloner;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.*;
import io.irontest.models.testrun.DataDrivenTestcaseRun;
import io.irontest.models.testrun.TestcaseIndividualRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;

import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME;

public class DataDrivenTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDrivenTestcaseRunner.class);

    public DataDrivenTestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, WireMockServer wireMockServer) {
        super(testcase, utilsDAO, testcaseRunDAO, LOGGER, wireMockServer);
    }

    @Override
    public TestcaseRun run() throws IOException {
        DataDrivenTestcaseRun testcaseRun = new DataDrivenTestcaseRun();
        Cloner cloner = new Cloner();

        preProcessing();

        startTestcaseRun(testcaseRun);

        DataTable dataTable = getTestcase().getDataTable();
        IronTestUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(getUdpNames(), dataTable);

        for (int dataTableRowIndex = 0; dataTableRowIndex < dataTable.getRows().size(); dataTableRowIndex++) {
            LinkedHashMap<String, DataTableCell> dataTableRow = dataTable.getRows().get(dataTableRowIndex);
            TestcaseIndividualRun individualRun = new TestcaseIndividualRun();
            testcaseRun.getIndividualRuns().add(individualRun);

            //  start test case individual run
            individualRun.setStartTime(new Date());
            LOGGER.info("Start individually running test case with data table row: " + individualRun.getCaption());
            individualRun.setResult(TestResult.PASSED);
            getTestcaseRunContext().setTestcaseIndividualRunStartTime(individualRun.getStartTime());
            getReferenceableStringProperties().put(IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME,
                    IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(individualRun.getStartTime()));
            individualRun.setCaption(dataTableRow.get(DataTableColumn.COLUMN_NAME_CAPTION).getValue());
            getReferenceableEndpointProperties().putAll(dataTable.getEndpointPropertiesInRow(dataTableRowIndex));
            getReferenceableStringProperties().putAll(dataTable.getStringPropertiesInRow(dataTableRowIndex));

            //  run test steps
            for (Teststep teststep : getTestcase().getTeststeps()) {
                Teststep clonedTeststep = cloner.deepClone(teststep);
                individualRun.getStepRuns().add(runTeststep(clonedTeststep));
            }

            //  test case individual run ends
            individualRun.setDuration(new Date().getTime() - individualRun.getStartTime().getTime());
            LOGGER.info("Finish individually running test case with data table row: " + individualRun.getCaption());
            for (TeststepRun teststepRun: individualRun.getStepRuns()) {
                if (TestResult.FAILED == teststepRun.getResult()) {
                    individualRun.setResult(TestResult.FAILED);
                    break;
                }
            }
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());
        LOGGER.info("Finish running test case: " + getTestcase().getName());
        for (TestcaseIndividualRun individualRun: testcaseRun.getIndividualRuns()) {
            if (TestResult.FAILED == individualRun.getResult()) {
                testcaseRun.setResult(TestResult.FAILED);
                break;
            }
        }

        //  persist test case run details into database
        getTestcaseRunDAO().insert(testcaseRun);

        return testcaseRun;
    }
}
