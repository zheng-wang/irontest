package io.irontest.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface VersionDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS version (version varchar(30), " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    //  project.version is a Maven built-in property, and it will be filtered during build
    @SqlUpdate("insert into version (version) select '${project.version}' where not exists (select * from version)")
    void insertVersionIfNotExists();
}
