package io.irontest.db;

import io.irontest.models.Endpoint;
import io.irontest.models.Environment;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Trevor Li on 7/5/15.
 */
@RegisterMapper(EnvironmentMapper.class)
public abstract class EnvironmentDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS environment_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS environment (id BIGINT DEFAULT environment_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL, description varchar(500)," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT ENVIRONMENT_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(name))")
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

    /**
     *
     * @param id
     * @return environment with all endpoints in it
     */
    @Transaction
    public Environment findById(long id) {
        Environment environment = _findById(id);
        List<Endpoint> endpoints = endpointDAO().findByEnvironmentId_PrimaryProperties(id);
        environment.setEndpoints(endpoints);
        return environment;
    }
}
