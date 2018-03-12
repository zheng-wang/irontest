package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Zheng on 9/03/2018.
 */
public abstract class TeststepRunDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS teststep_run_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_run (id BIGINT DEFAULT teststep_run_sequence.NEXTVAL PRIMARY KEY, " +
            "testcase_run_id BIGINT, testcase_individualrun_id BIGINT, teststep CLOB NOT NULL, response CLOB, " +
            "info_message CLOB, error_message CLOB, assertion_verifications CLOB, " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (testcase_individualrun_id) REFERENCES testcase_individualrun(id) ON DELETE CASCADE, " +
            "CONSTRAINT TESTSTEP_RUN_EXCLUSIVE_PARENT_CONSTRAINT " +
                "CHECK((testcase_run_id is null AND testcase_individualrun_id is not null) OR " +
                    "(testcase_run_id is not null AND testcase_individualrun_id is null)))")
    public abstract void createTableIfNotExists();
}
