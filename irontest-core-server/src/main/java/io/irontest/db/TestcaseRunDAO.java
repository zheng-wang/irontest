package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import io.irontest.models.testrun.TeststepRun;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Date;

/**
 * Created by Zheng on 24/07/2016.
 */
@RegisterMapper(TestcaseRunMapper.class)
public abstract class TestcaseRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_run (id BIGINT DEFAULT testcase_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT NOT NULL, testcase_name varchar(200) NOT NULL, testcase_folderpath CLOB NOT NULL," +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @CreateSqlObject
    protected abstract TeststepRunDAO teststepRunDAO();

    @SqlUpdate("insert into testcase_run " +
            "(testcase_id, testcase_name, testcase_folderpath, starttime, duration, result) values " +
            "(:testcase_id, :testcase_name, :testcase_folderpath, :starttime, :duration, :result)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcase_id") long testcaseId, @Bind("testcase_name") String testcaseName,
                                    @Bind("testcase_folderpath") String testcaseFolderPath,
                                    @Bind("starttime") Date startTime, @Bind("duration") long duration,
                                    @Bind("result") String result);

    @Transaction
    public void insert(RegularTestcaseRun testcaseRun) throws JsonProcessingException {
        long id = _insert(testcaseRun.getTestcaseId(), testcaseRun.getTestcaseName(),
                testcaseRun.getTestcaseFolderPath(), testcaseRun.getStartTime(), testcaseRun.getDuration(),
                testcaseRun.getResult().toString());
        testcaseRun.setId(id);

        for (TeststepRun teststepRun: testcaseRun.getStepRuns()) {
            teststepRunDAO().insert_NoTransaction(id, teststepRun);
        }
    }

    @SqlQuery("select * from testcase_run where id = :id")
    public abstract TestcaseRun _findById(@Bind("id") long id);

    @SqlQuery("select top 1 * from testcase_run where testcase_id = :testcaseId order by starttime desc")
    public abstract TestcaseRun _findLastByTestcaseId(@Bind("testcaseId") long testcaseId);

    @Transaction
    public TestcaseRun findById(long id) {
        RegularTestcaseRun testcaseRun = (RegularTestcaseRun) _findById(id);
        testcaseRun.setStepRuns(teststepRunDAO().findByTestcaseRunId_NoTransaction(id));
        return testcaseRun;
    }

    @Transaction
    public TestcaseRun findLastByTestcaseId(long testcaseId) {
        RegularTestcaseRun testcaseRun = (RegularTestcaseRun) _findLastByTestcaseId(testcaseId);
        if (testcaseRun != null) {
            testcaseRun.setStepRuns(teststepRunDAO().findByTestcaseRunId_NoTransaction(testcaseRun.getId()));
        }
        return testcaseRun;
    }
}