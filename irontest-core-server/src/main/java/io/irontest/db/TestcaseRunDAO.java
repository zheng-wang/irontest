package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Zheng on 24/07/2016.
 */
public interface TestcaseRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_run (id BIGINT DEFAULT testcase_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_id BIGINT NOT NULL, testcase_name varchar(200) NOT NULL, starttime TIMESTAMP NOT NULL, " +
            "duration BIGINT NOT NULL, result varchar(15) NOT NULL, stepruns CLOB NOT NULL, " +
            "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();
}
