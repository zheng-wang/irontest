package io.irontest.db;

import io.irontest.models.Environment;
import io.irontest.models.endpoint.Endpoint;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(EnvironmentMapper.class)
public interface EnvironmentDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS environment_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS environment (id BIGINT DEFAULT environment_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, description CLOB," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT ENVIRONMENT_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into environment values ()")
    @GetGeneratedKeys
    long _insert();

    @SqlUpdate("update environment set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default long insert() {
        long id = _insert();
        updateNameForInsert(id, "Environment " + id);
        return id;
    }

    @SqlUpdate("update environment set name = :name, description = :description, updated = CURRENT_TIMESTAMP where id = :id")
    void update(@BindBean Environment environment);

    @SqlUpdate("delete from environment where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from environment")
    List<Environment> findAll();

    @SqlQuery("select * from environment where id = :id")
    Environment _findById(@Bind("id") long id);

    /**
     *
     * @param id
     * @return environment with all endpoints in it
     */
    @Transaction
    default Environment findById_EnvironmentEditView(long id) {
        Environment environment = _findById(id);
        List<Endpoint> endpoints = endpointDAO().findByEnvironmentId_EnvironmentEditView(id);
        environment.setEndpoints(endpoints);
        return environment;
    }
}
