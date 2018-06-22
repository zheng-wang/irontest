package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.testrun.*;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.Date;
import java.util.List;

@RegisterRowMapper(TestcaseRunMapper.class)
public interface TestcaseRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_run (id BIGINT DEFAULT testcase_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT NOT NULL, testcase_name varchar(200) NOT NULL, testcase_folderpath CLOB NOT NULL," +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into testcase_run " +
            "(testcase_id, testcase_name, testcase_folderpath, starttime, duration, result) values " +
            "(:testcase_id, :testcase_name, :testcase_folderpath, :starttime, :duration, :result)")
    @GetGeneratedKeys
    long _insert(@Bind("testcase_id") long testcaseId, @Bind("testcase_name") String testcaseName,
                 @Bind("testcase_folderpath") String testcaseFolderPath,
                 @Bind("starttime") Date startTime, @Bind("duration") long duration,
                 @Bind("result") String result);

    @Transaction
    default void insert(TestcaseRun testcaseRun) throws JsonProcessingException {
        long id = _insert(testcaseRun.getTestcaseId(), testcaseRun.getTestcaseName(),
                testcaseRun.getTestcaseFolderPath(), testcaseRun.getStartTime(), testcaseRun.getDuration(),
                testcaseRun.getResult().toString());
        testcaseRun.setId(id);

        if (testcaseRun instanceof RegularTestcaseRun) {
            RegularTestcaseRun regularTestcaseRun = (RegularTestcaseRun) testcaseRun;
            for (TeststepRun teststepRun: regularTestcaseRun.getStepRuns()) {
                teststepRunDAO().insert(id, null, teststepRun);
            }
        } else if (testcaseRun instanceof DataDrivenTestcaseRun) {
            DataDrivenTestcaseRun dataDrivenTestcaseRun = (DataDrivenTestcaseRun) testcaseRun;
            for (TestcaseIndividualRun testcaseIndividualRun: dataDrivenTestcaseRun.getIndividualRuns()) {
                testcaseIndividualRunDAO().insert(id, testcaseIndividualRun);
            }
        }
    }

    @SqlQuery("select * from testcase_run where id = :id")
    TestcaseRun _findById(@Bind("id") long id);

    @SqlQuery("select top 1 * from testcase_run where testcase_id = :testcaseId order by starttime desc")
    TestcaseRun _findLastByTestcaseId(@Bind("testcaseId") long testcaseId);

    @Transaction
    default TestcaseRun findById(long id) {
        TestcaseRun testcaseRun = _findById(id);
        return resolveTestcaseRun(testcaseRun);
    }

    @Transaction
    default TestcaseRun findLastByTestcaseId(long testcaseId) {
        TestcaseRun testcaseRun = _findLastByTestcaseId(testcaseId);
        return resolveTestcaseRun(testcaseRun);
    }

    default TestcaseRun resolveTestcaseRun(TestcaseRun testcaseRun) {
        if (testcaseRun != null) {
            long runId = testcaseRun.getId();
            List<TestcaseIndividualRun> individualRuns = testcaseIndividualRunDAO().findByTestcaseRunId(runId);
            if (individualRuns.size() > 0) {     //  it is a data driven test case run
                DataDrivenTestcaseRun dataDrivenTestcaseRun = new DataDrivenTestcaseRun(testcaseRun);
                dataDrivenTestcaseRun.setIndividualRuns(individualRuns);
                testcaseRun = dataDrivenTestcaseRun;
            } else {                             //  it is a regular test case run
                RegularTestcaseRun regularTestcaseRun = new RegularTestcaseRun(testcaseRun);
                regularTestcaseRun.setStepRuns(teststepRunDAO().findByTestcaseRunId(runId));
                testcaseRun = regularTestcaseRun;
            }
        }

        return testcaseRun;
    }
}