package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Date;
import java.util.List;

@RegisterRowMapper(TeststepRunMapper.class)
public interface TeststepRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS teststep_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_run (id BIGINT DEFAULT teststep_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_run_id BIGINT NOT NULL, testcase_individualrun_id BIGINT, teststep CLOB NOT NULL, response CLOB, " +
            "info_message CLOB, error_message CLOB, assertion_verifications CLOB, " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (testcase_individualrun_id) REFERENCES testcase_individualrun(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into teststep_run (testcase_run_id, testcase_individualrun_id, teststep, response, info_message," +
            " error_message, assertion_verifications, starttime, duration, result) values (" +
            ":testcaseRunId, :testcaseIndividualRunId, :teststep, :response, :infoMessage, :errorMessage, " +
            ":assertionVerifications, :startTime, :duration, :result)")
    @GetGeneratedKeys
    long _insert(@Bind("testcaseRunId") long testcaseRunId,
                 @Bind("testcaseIndividualRunId") Long testcaseIndividualRunId,
                 @Bind("teststep") String teststep, @Bind("response") String response,
                 @Bind("infoMessage") String infoMessage, @Bind("errorMessage") String errorMessage,
                 @Bind("assertionVerifications") String assertionVerifications,
                 @Bind("startTime") Date startTime, @Bind("duration") long duration,
                 @Bind("result") String result);

    default void insert(long testcaseRunId, Long testcaseIndividualRunId,
                        TeststepRun teststepRun) throws JsonProcessingException {
        //  remove contents that are not to be serialized into the teststep column
        Teststep teststep = teststepRun.getTeststep();
        teststep.getAssertions().clear();
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null) {
            endpoint.setPassword(null);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        IronTestUtils.addMixInsForWireMock(objectMapper);
        long id = _insert(testcaseRunId, testcaseIndividualRunId, objectMapper.writeValueAsString(teststep),
                objectMapper.writeValueAsString(teststepRun.getResponse()), teststepRun.getInfoMessage(),
                teststepRun.getErrorMessage(), objectMapper.writeValueAsString(teststepRun.getAssertionVerifications()),
                teststepRun.getStartTime(), teststepRun.getDuration(), teststepRun.getResult().toString());
        teststepRun.setId(id);
    }

    @SqlQuery("select * from teststep_run where testcase_run_id = :testcaseRunId")
    List<TeststepRun> findByTestcaseRunId(@Bind("testcaseRunId") long testcaseRunId);

    @SqlQuery("select * from teststep_run where id = :id")
    TeststepRun findById(@Bind("id") long id);

    @SqlQuery("select * from teststep_run where testcase_individualrun_id = :testcaseIndividualRunId")
    List<TeststepRun> findByTestcaseIndividualRunId(@Bind("testcaseIndividualRunId") long testcaseIndividualRunId);
}