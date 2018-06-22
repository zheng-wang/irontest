package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.testrun.TestcaseIndividualRun;
import io.irontest.models.testrun.TeststepRun;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.Date;
import java.util.List;

@RegisterRowMapper(TestcaseIndividualRunMapper.class)
public interface TestcaseIndividualRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_individualrun_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_individualrun (" +
            "id BIGINT DEFAULT testcase_individualrun_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_run_id BIGINT NOT NULL, caption VARCHAR(500), " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into testcase_individualrun (testcase_run_id, caption, starttime, duration, result) values (" +
            ":testcaseRunId, :caption, :startTime, :duration, :result)")
    @GetGeneratedKeys
    long _insert(@Bind("testcaseRunId") long testcaseRunId, @Bind("caption") String caption,
                 @Bind("startTime") Date startTime, @Bind("duration") long duration,
                 @Bind("result") String result);

    @Transaction
    default void insert(long testcaseRunId, TestcaseIndividualRun testcaseIndividualRun) throws JsonProcessingException {
        long id = _insert(testcaseRunId, testcaseIndividualRun.getCaption(), testcaseIndividualRun.getStartTime(),
                testcaseIndividualRun.getDuration(), testcaseIndividualRun.getResult().toString());

        for (TeststepRun teststepRun: testcaseIndividualRun.getStepRuns()) {
            teststepRunDAO().insert(testcaseRunId, id, teststepRun);
        }
    }

    @SqlQuery("select * from testcase_individualrun where testcase_run_id = :testcaseRunId")
    List<TestcaseIndividualRun> _findByTestcaseRunId(@Bind("testcaseRunId") long testcaseRunId);

    @Transaction
    default List<TestcaseIndividualRun> findByTestcaseRunId(long testcaseRunId) {
        List<TestcaseIndividualRun> individualRuns = _findByTestcaseRunId(testcaseRunId);
        for (TestcaseIndividualRun individualRun: individualRuns) {
            individualRun.setStepRuns(teststepRunDAO().findByTestcaseIndividualRunId(individualRun.getId()));
        }
        return individualRuns;
    }
}