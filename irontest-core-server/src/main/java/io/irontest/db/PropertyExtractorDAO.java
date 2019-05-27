package io.irontest.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

public interface PropertyExtractorDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS property_extractor_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS property_extractor (" +
            "id BIGINT DEFAULT property_extractor_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL, type VARCHAR(50) NOT NULL, path CLOB NOT NULL," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT PROPERTY_EXTRACTOR_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    void createTableIfNotExists();
}
