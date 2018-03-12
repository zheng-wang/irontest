package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Zheng on 9/03/2018.
 */
public abstract class TestcaseIndividualRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS testcase_individualrun_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS testcase_individualrun (" +
            "id BIGINT DEFAULT testcase_individualrun_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_run_id BIGINT NOT NULL, caption VARCHAR(500), " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();
}