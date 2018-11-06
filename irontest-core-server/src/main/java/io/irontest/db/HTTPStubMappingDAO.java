package io.irontest.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface HTTPStubMappingDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS httpstubmapping_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS httpstubmapping (" +
            "id BIGINT DEFAULT httpstubmapping_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT, json CLOB NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE)")
    void createTableIfNotExists();
}