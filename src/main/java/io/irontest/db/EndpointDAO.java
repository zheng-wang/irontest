package io.irontest.db;

import io.irontest.models.Endpoint;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@RegisterMapper(EndpointMapper.class)
public interface EndpointDAO {
    @SqlUpdate("create table IF NOT EXISTS endpoint (id INT PRIMARY KEY auto_increment, environment_id int, " +
            "name varchar(200) UNIQUE, type varchar(20), description varchar(500), " +
            "url varchar(500), username varchar(200), password varchar(200), " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (environment_id) REFERENCES environment(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (environment_id, name, type, description, url, username, password) values (" +
            ":environmentId, :name, :type, :description, :url, :username, ENCRYPT('AES', '8888', STRINGTOUTF8(:password)))")
    @GetGeneratedKeys
    long insertManagedEndpoint(@BindBean Endpoint endpoint);

    @SqlUpdate("insert into endpoint (type, description, url) values (:type, :description, :url)")
    @GetGeneratedKeys
    long insertUnmanagedEndpoint(@BindBean Endpoint endpoint);

    @SqlUpdate("update endpoint set name = :name, description = :description, url = :url, username = :username, " +
            "password = ENCRYPT('AES', '8888', STRINGTOUTF8(:password)), updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Endpoint endpoint);

    @SqlUpdate("delete from endpoint where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from endpoint where id = :id")
    Endpoint findById(@Bind("id") long id);

    @SqlQuery("select * from endpoint where name = :name")
    Endpoint findByName(@Bind("name") String name);

    @SqlQuery("select id, environment_id, name, type, description from endpoint where environment_id = :environmentId")
    List<Endpoint> findByEnvironmentId_PrimaryProperties(@Bind("environmentId") long environmentId);
}
