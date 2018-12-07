package io.irontest.db;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(HTTPStubMappingMapper.class)
public interface HTTPStubMappingDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS httpstubmapping_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS httpstubmapping (" +
            "id BIGINT DEFAULT httpstubmapping_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT, number SMALLINT NOT NULL, " +
            "spec_json CLOB NOT NULL DEFAULT '{ \"request\": { \"url\": \"/\", \"method\": \"GET\" }, \"response\": { \"status\": 200 } }', " +
            "expected_hit_count SMALLINT NOT NULL DEFAULT 1, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE," +
            "CONSTRAINT HTTPSTUBMAPPING_UNIQUE_NUMBER_CONSTRAINT UNIQUE(testcase_id, number))")
    void createTableIfNotExists();

    @SqlQuery("select * from httpstubmapping where testcase_id = :testcaseId order by number")
    List<HTTPStubMapping> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select * from httpstubmapping where id = :httpStubId")
    HTTPStubMapping findById(@Bind("httpStubId") long httpStubId);

    @SqlUpdate("insert into httpstubmapping (testcase_id, number) values (:testcaseId, (" +
            "select coalesce(max(number), 0) + 1 from httpstubmapping where testcase_id = :testcaseId))")
    @GetGeneratedKeys
    long insert(@Bind("testcaseId") long testcaseId);

    @SqlUpdate("delete from httpstubmapping where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlUpdate("update httpstubmapping set spec_json = :specJson, expected_hit_count = :expectedHitCount, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    void _update(@Bind("id") long id, @Bind("specJson") String specJson, @Bind("expectedHitCount") short expectedHitCount);

    default void update(HTTPStubMapping stub) {
        _update(stub.getId(), StubMapping.buildJsonStringFor(stub.getSpec()), stub.getExpectedHitCount());
    }
}