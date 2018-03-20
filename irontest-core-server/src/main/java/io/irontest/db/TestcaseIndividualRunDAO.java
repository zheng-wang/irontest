package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.testrun.TestcaseIndividualRun;
import io.irontest.models.testrun.TeststepRun;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.Date;

/**
 * Created by Zheng on 9/03/2018.
 */
public abstract class TestcaseIndividualRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_individualrun_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_individualrun (" +
            "id BIGINT DEFAULT testcase_individualrun_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_run_id BIGINT NOT NULL, caption VARCHAR(500), " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract TeststepRunDAO teststepRunDAO();

    @SqlUpdate("insert into testcase_individualrun (testcase_run_id, caption, starttime, duration, result) values (" +
            ":testcaseRunId, :caption, :startTime, :duration, :result)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcaseRunId") long testcaseRunId, @Bind("caption") String caption,
                                    @Bind("startTime") Date startTime, @Bind("duration") long duration,
                                    @Bind("result") String result);

    public void insert_NoTransaction(long testcaseRunId, TestcaseIndividualRun testcaseIndividualRun) throws JsonProcessingException {
        long id = _insert(testcaseRunId, testcaseIndividualRun.getCaption(), testcaseIndividualRun.getStartTime(),
                testcaseIndividualRun.getDuration(), testcaseIndividualRun.getResult().toString());

        for (TeststepRun teststepRun: testcaseIndividualRun.getStepRuns()) {
            teststepRunDAO().insert_NoTransaction(testcaseRunId, id, teststepRun);
        }
    }
}