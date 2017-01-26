package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.Testcase;
import io.irontest.models.TestcaseRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRun;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 24/07/2016.
 */
@RegisterMapper(TestcaseRunMapper.class)
public abstract class TestcaseRunDAO {
    //  object mapper from Dropwizard environment
    private ObjectMapper environmentObjectMapper;

    public void setEnvironmentObjectMapper(ObjectMapper environmentObjectMapper) {
        this.environmentObjectMapper = environmentObjectMapper;
    }

    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_run (id BIGINT DEFAULT testcase_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT NOT NULL, testcase_name varchar(200) NOT NULL, testcase_folderpath CLOB NOT NULL," +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "stepruns CLOB NOT NULL, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into testcase_run " +
            "(testcase_id, testcase_name, testcase_folderpath, starttime, duration, result, stepruns) values " +
            "(:testcase_id, :testcase_name, :testcase_folderpath, :starttime, :duration, :result, :stepruns)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcase_id") long testcaseId, @Bind("testcase_name") String testcaseName,
                                    @Bind("testcase_folderpath") String testcaseFolderPath,
                                    @Bind("starttime") Date startTime, @Bind("duration") long duration,
                                    @Bind("result") String result, @Bind("stepruns") String stepRunsJSON);

    @Transaction
    public void insert(TestcaseRun testcaseRun) throws JsonProcessingException {
        //  remove contents that are not to be serialized into the stepRunsJSON
        List<TeststepRun> stepRuns = testcaseRun.getStepRuns();
        for (TeststepRun stepRun : stepRuns) {
            Teststep step = stepRun.getTeststep();
            step.getAssertions().clear();
            Endpoint endpoint = step.getEndpoint();
            if (endpoint != null) {
                endpoint.setPassword(null);
            }
        }

        //  serialize stepRuns into JSON string
        String stepRunsJSON = environmentObjectMapper.writeValueAsString(stepRuns);
        Testcase testcase = testcaseRun.getTestcase();
        long id = _insert(testcase.getId(), testcase.getName(), testcase.getFolderPath(), testcaseRun.getStartTime(),
                testcaseRun.getDuration(), testcaseRun.getResult().toString(), stepRunsJSON);
        testcaseRun.setId(id);
    }

    @SqlQuery("select * from testcase_run where id = :id")
    public abstract TestcaseRun findById(@Bind("id") long id);
}
