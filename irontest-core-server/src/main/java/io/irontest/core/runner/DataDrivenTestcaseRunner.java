package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rits.cloning.Cloner;
import io.irontest.db.TestcaseRunDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.DataTable;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.testrun.DataDrivenTestcaseRun;
import io.irontest.models.testrun.TestcaseIndividualRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.WaitTeststepProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.irontest.IronTestConstants.IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME;

/**
 * Created by Zheng on 15/03/2018.
 */
public class DataDrivenTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDrivenTestcaseRunner.class);

    private DataTable dataTable;

    public DataDrivenTestcaseRunner(Testcase testcase, List<UserDefinedProperty> testcaseUDPs, DataTable dataTable,
                                    TeststepDAO teststepDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO) {
        super(testcase, testcaseUDPs, teststepDAO, utilsDAO, testcaseRunDAO, LOGGER);
        this.dataTable = dataTable;
    }

    @Override
    public TestcaseRun run() throws JsonProcessingException {
        DataDrivenTestcaseRun testcaseRun = new DataDrivenTestcaseRun();
        Cloner cloner = new Cloner();

        preProcessingForIIBTestcase();
        startTestcaseRun(testcaseRun);

        for (LinkedHashMap<String, Object> dataTableRow: dataTable.getRows()) {
            TestcaseIndividualRun individualRun = new TestcaseIndividualRun();
            testcaseRun.getIndividualRuns().add(individualRun);

            //  start test case individual run
            individualRun.setStartTime(new Date());
            individualRun.setResult(TestResult.PASSED);
            getTestcaseRunContext().setTestcaseIndividualRunStartTime(individualRun.getStartTime());
            if (isTestcaseHasWaitForProcessingCompletionAction()) {
                long secondFraction = individualRun.getStartTime().getTime() % 1000;   //  milliseconds
                long millisecondsUntilNextSecond = 1000 - secondFraction;
                Teststep waitStep = getTestcase().getTeststeps().get(0);
                waitStep.setName("Wait " + millisecondsUntilNextSecond + " milliseconds");
                waitStep.setOtherProperties(new WaitTeststepProperties(millisecondsUntilNextSecond));
            }
            getReferenceableStringProperties().put(IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME,
                    IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(individualRun.getStartTime()));
            //  from data table row:
            //    add string properties to referenceableStringProperties,
            //    set caption to individualRun, and
            //    add endpoint properties to referenceableEndpointProperties.
            for (Map.Entry<String, Object> property: dataTableRow.entrySet()) {
                if ("String".equals(dataTable.getColumnTypeByName(property.getKey()))) {
                    if ("Caption".equals(property.getKey())) {
                        individualRun.setCaption((String) property.getValue());
                    } else {
                        getReferenceableStringProperties().put(property.getKey(), (String) property.getValue());
                    }
                } else {
                    getReferenceableEndpointProperties().put(property.getKey(), (Endpoint) property.getValue());
                }
            }
            LOGGER.info("Start individually running test case with data table row: " + individualRun.getCaption());

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
