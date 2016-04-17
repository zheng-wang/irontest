package io.irontest.db;

import io.irontest.models.Environment;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 7/5/15.
 */
@RegisterMapper(EnvironmentMapper.class)
public interface EnvironmentDAO {
    @SqlUpdate("create table IF NOT EXISTS environment (id INT PRIMARY KEY auto_increment, name varchar(200) UNIQUE not null, description varchar(500)," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into environment (name, description) values (:name, :description)")
    @GetGeneratedKeys
    long insert(@BindBean Environment environment);

    @SqlUpdate("update environment set name = :name, description = :description, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Environment environment);

    @SqlUpdate("delete from environment where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from environment")
    List<Environment> findAll();

    @SqlQuery("select * from environment where id = :id")
    Environment findById(@Bind("id") long id);
}
