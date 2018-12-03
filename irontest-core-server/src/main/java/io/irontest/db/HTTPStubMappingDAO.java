package io.irontest.db;

import io.irontest.models.HTTPStubMapping;
import io.irontest.models.UserDefinedProperty;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

@RegisterRowMapper(HTTPStubMappingMapper.class)
public interface HTTPStubMappingDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS httpstubmapping_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS httpstubmapping (" +
            "id BIGINT DEFAULT httpstubmapping_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT, number SMALLINT NOT NULL, spec_json CLOB NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE," +
            "CONSTRAINT HTTPSTUBMAPPING_UNIQUE_NUMBER_CONSTRAINT UNIQUE(testcase_id, number))")
    void createTableIfNotExists();

    @SqlQuery("select * from httpstubmapping where testcase_id = :testcaseId order by number")
    List<HTTPStubMapping> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select * from httpstubmapping where id = :httpStubId")
    HTTPStubMapping findById(@Bind("httpStubId") long httpStubId);

    @SqlUpdate("insert into httpstubmapping (testcase_id, number, spec_json) values (:testcaseId, (" +
            "select coalesce(max(number), 0) + 1 from httpstubmapping where testcase_id = :testcaseId)," +
            ":specJson)")
    @GetGeneratedKeys
    long _insert(@Bind("testcaseId") long testcaseId, @Bind("specJson") String specJson);

    @Transaction
    default HTTPStubMapping insert(long testcaseId) {
        String specJson = "{ \"request\": { \"method\": \"GET\" }, \"response\": { \"status\": 200 } }";
        long id = _insert(testcaseId, specJson);
        return findById(id);
    }
}