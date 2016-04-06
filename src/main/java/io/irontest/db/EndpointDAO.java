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
    @SqlUpdate("create table IF NOT EXISTS endpoint (id INT PRIMARY KEY auto_increment, name varchar(50) UNIQUE not null, description varchar(500), handler varchar(50), " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (name, description, handler) values (:name, :description, :handler)")
    @GetGeneratedKeys
    long insert(@BindBean Endpoint endpoint);

    @SqlUpdate("update endpoint set name = :name, description = :description, handler = :handler, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Endpoint endpoint);

    @SqlUpdate("delete from endpoint where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from endpoint")
    List<Endpoint> findAll();

    @SqlQuery("select * from endpoint where id = :id")
    Endpoint findById(@Bind("id") long id);
}
