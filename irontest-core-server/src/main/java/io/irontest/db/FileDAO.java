package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.io.InputStream;

/**
 * Created by zhenw9 on 22/06/2016.
 */
public interface FileDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS file_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS file (id BIGINT DEFAULT file_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL, data BLOB," +
            "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into file (name, data) values (:name, :data)")
    @GetGeneratedKeys
    long insert(@Bind("name") String name, @Bind("data") InputStream data);

    @SqlQuery("select name from file where id = :id")
    String getNameById(@Bind("id") long id);
}