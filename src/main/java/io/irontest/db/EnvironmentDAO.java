package io.irontest.db;

import io.irontest.models.Endpoint;
import io.irontest.models.Environment;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 7/5/15.
 */
@RegisterMapper(EnvironmentMapper.class)
public abstract class EnvironmentDAO {
    @SqlUpdate("create table IF NOT EXISTS environment (id INT PRIMARY KEY auto_increment, name varchar(200) UNIQUE not null, description varchar(500)," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into environment (name, description) values (:name, :description)")
    @GetGeneratedKeys
    public abstract long insert(@BindBean Environment environment);

    @SqlUpdate("update environment set name = :name, description = :description, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Environment environment);

    @SqlUpdate("delete from environment where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select * from environment")
    public abstract List<Environment> findAll();

    @SqlQuery("select * from environment where id = :id")
    protected abstract Environment _findById(@Bind("id") long id);

    @CreateSqlObject
    protected abstract EndpointDAO endpointDAO();

    @Transaction
    public Environment findById(long id) {
        Environment environment = _findById(id);
        List<Endpoint> endpoints = endpointDAO().findByEnvironmentId_PrimaryProperties(id);
        environment.setEndpoints(endpoints);
        return environment;
    }
}
