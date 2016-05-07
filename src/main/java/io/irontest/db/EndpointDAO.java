package io.irontest.db;

import io.irontest.models.Endpoint;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;
import static io.irontest.IronTestConstants.PASSWORD_ENCRYPTION_KEY;

/**
 * Created by Trevor Li on 6/30/15.
 */
@RegisterMapper(EndpointMapper.class)
public abstract class EndpointDAO {
    @SqlUpdate("create table IF NOT EXISTS endpoint (id IDENTITY PRIMARY KEY, environment_id int, " +
            "name varchar(200) NOT NULL, type varchar(20) NOT NULL, description varchar(500), " +
            "url varchar(500), username varchar(200), password varchar(200), " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (environment_id) REFERENCES environment(id) ON DELETE CASCADE, " +
            "CONSTRAINT ENDPOINT_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(environment_id, name))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (environment_id, name, type, description, url, username, password) values (" +
            ":evId, :ep.name, :ep.type, :ep.description, :ep.url, " +
            ":ep.username, ENCRYPT('AES', '" + PASSWORD_ENCRYPTION_KEY + "', STRINGTOUTF8(:ep.password)))")
    @GetGeneratedKeys
    protected abstract long _insertManagedEndpoint(@BindBean("ep") Endpoint endpoint, @Bind("evId") long environmentId);

    public long insertManagedEndpoint(Endpoint endpoint) {
        return _insertManagedEndpoint(endpoint, endpoint.getEnvironment().getId());
    }

    @SqlUpdate("insert into endpoint (name, type, description, url) values (:name, :type, :description, :url)")
    @GetGeneratedKeys
    public abstract long insertUnmanagedEndpoint(@BindBean Endpoint endpoint);

    @SqlUpdate("update endpoint set environment_id = :evId, name = :ep.name, description = :ep.description, " +
            "url = :ep.url, username = :ep.username, password = CASE " +
                "WHEN COALESCE(password, '') <> COALESCE(:ep.password, '') " +
                    "THEN ENCRYPT('AES', '" + PASSWORD_ENCRYPTION_KEY + "', STRINGTOUTF8(:ep.password)) " +
                "ELSE password END, " +
            "updated = CURRENT_TIMESTAMP where id = :ep.id")
    protected abstract int _update(@BindBean("ep") Endpoint endpoint, @Bind("evId") Long environmentId);

    public int update(Endpoint endpoint) {
        return _update(endpoint, endpoint.getEnvironment() == null ? null : endpoint.getEnvironment().getId());
    }

    @SqlUpdate("delete from endpoint where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery(
            "select ep.*, ev.name as environment_name " +
            "from endpoint ep left outer join environment ev on ep.environment_id = ev.id " +
            "where ep.id = :id")
    public abstract Endpoint findById(@Bind("id") long id);

    @SqlQuery("select * from endpoint where name = :name")
    public abstract Endpoint findByName(@Bind("name") String name);

    @SqlQuery("select id, environment_id, name, type, description from endpoint where environment_id = :environmentId")
    public abstract List<Endpoint> findByEnvironmentId_PrimaryProperties(@Bind("environmentId") long environmentId);

    @SqlQuery(
            "select ep.id, ep.environment_id, ev.name as environment_name, ep.name, ep.description " +
            "from endpoint ep left outer join environment ev on ep.environment_id = ev.id " +
            "where ep.type = :endpointType and ep.environment_id is not null")
    public abstract List<Endpoint> findManagedEndpointsByType(@Bind("endpointType") String endpointType);
}
