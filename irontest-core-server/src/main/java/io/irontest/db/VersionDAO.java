package io.irontest.db;

import io.irontest.Version;       //  compilation in IDE relies on the Maven generated Version.java under target/generated-sources/java-templates
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface VersionDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS version (version varchar(30), " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into version (version) select '" + Version.VERSION + "' where not exists (select * from version)")
    void insertVersionIfNotExists();
}
