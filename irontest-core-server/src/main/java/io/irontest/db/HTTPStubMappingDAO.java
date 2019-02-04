package io.irontest.db;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
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
            "testcase_id BIGINT, number SMALLINT NOT NULL, " +
            "spec_json CLOB NOT NULL DEFAULT '{ \"request\": { \"url\": \"/\", \"method\": \"GET\" }, \"response\": { \"status\": 200 } }', " +
            "request_body_main_pattern_value CLOB, expected_hit_count SMALLINT NOT NULL DEFAULT 1, " +
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

    @SqlUpdate("insert into httpstubmapping (testcase_id, number, spec_json, request_body_main_pattern_value, " +
            "expected_hit_count) values (:testcaseId, :number, :specJson, :requestBodyMainPatternValue, :expectedHitCount)")
    void insert(@Bind("testcaseId") long testcaseId, @Bind("number") short number, @Bind("specJson") String specJson,
                @Bind("requestBodyMainPatternValue") String requestBodyMainPatternValue,
                @Bind("expectedHitCount") short expectedHitCount);

    @SqlUpdate("delete from httpstubmapping where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlUpdate("update httpstubmapping set spec_json = :specJson, " +
            "request_body_main_pattern_value = :requestBodyMainPatternValue, expected_hit_count = :expectedHitCount, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    void _update(@Bind("id") long id, @Bind("specJson") String specJson,
                 @Bind("requestBodyMainPatternValue") String requestBodyMainPatternValue,
                 @Bind("expectedHitCount") short expectedHitCount);

    default void update(HTTPStubMapping stub) {
        _update(stub.getId(), StubMapping.buildJsonStringFor(stub.getSpec()), stub.getRequestBodyMainPatternValue(),
                stub.getExpectedHitCount());
    }

    @SqlQuery("select * from httpstubmapping where testcase_id = :testcaseId and number = :number")
    HTTPStubMapping findByNumber(@Bind("testcaseId") long testcaseId, @Bind("number") short number);

    @SqlUpdate("update httpstubmapping set number = :newNumber, updated = CURRENT_TIMESTAMP where id = :id")
    void updateNumberById(@Bind("id") long id, @Bind("newNumber") short newNumber);

    @SqlUpdate("update httpstubmapping set number = case when :direction = 'up' then number - 1 else number + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and number >= :firstNumber and number <= :lastNumber")
    void batchMove(@Bind("testcaseId") long testcaseId,
                   @Bind("firstNumber") short firstNumber,
                   @Bind("lastNumber") short lastNumber,
                   @Bind("direction") String direction);

    @Transaction
    default void moveInTestcase(long testcaseId, short fromNumber, short toNumber) {
        if (fromNumber != toNumber) {
            long draggedStubId = findByNumber(testcaseId, fromNumber).getId();

            //  shelve the dragged stub first
            updateNumberById(draggedStubId, (short) -1);

            if (fromNumber < toNumber) {
                batchMove(testcaseId, (short) (fromNumber + 1), toNumber, "up");
            } else {
                batchMove(testcaseId, toNumber, (short) (fromNumber - 1), "down");
            }

            //  move the dragged stub last
            updateNumberById(draggedStubId, toNumber);
        }
    }

    /**
     * Copy HTTP stubs from source test case to target test case.
     * @param sourceTestcaseId
     * @param targetTestcaseId
     */
    @SqlUpdate("insert into httpstubmapping (testcase_id, number, spec_json, request_body_main_pattern_value, expected_hit_count) " +
            "select :targetTestcaseId, number, spec_json, request_body_main_pattern_value, expected_hit_count from httpstubmapping where testcase_id = :sourceTestcaseId")
    void duplicateByTestcase(@Bind("sourceTestcaseId") long sourceTestcaseId,
                             @Bind("targetTestcaseId") long targetTestcaseId);

    default void insertByImport(long testcaseId, HTTPStubMapping stub) {
        insert(testcaseId, stub.getNumber(), StubMapping.buildJsonStringFor(stub.getSpec()),
                stub.getRequestBodyMainPatternValue(), stub.getExpectedHitCount());
    }
}