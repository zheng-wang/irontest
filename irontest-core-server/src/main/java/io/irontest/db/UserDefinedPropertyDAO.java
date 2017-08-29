package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 29/08/2017.
 */
public interface UserDefinedPropertyDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS udp_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS udp (" +
            "id BIGINT DEFAULT udp_sequence.NEXTVAL PRIMARY KEY, testcase_id BIGINT, " +
            "name VARCHAR(200) NOT NULL, value VARCHAR(500), " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT UDP_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name))")
    void createTableIfNotExists();
}
