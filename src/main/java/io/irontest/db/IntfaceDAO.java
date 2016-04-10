package io.irontest.db;

import io.irontest.models.Intface;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 7/4/15.
 */
@RegisterMapper(IntfaceMapper.class)
public abstract class IntfaceDAO {
    @SqlUpdate("create table IF NOT EXISTS intface (id INT PRIMARY KEY auto_increment, name varchar(50) UNIQUE not null, description varchar(500), deftype varchar(50), defurl varchar(200)," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into intface (name, description, deftype, defurl) values (:name, :description, :deftype, :defurl)")
    @GetGeneratedKeys
    public abstract long insert(@BindBean Intface intface);

    @SqlUpdate("update intface set name = :name, description = :description, deftype = :deftype, defurl = :defurl, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Intface intface);

    @SqlUpdate("delete from intface where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select * from intface")
    public abstract List<Intface> findAll();

    @SqlQuery("select * from intface where id = :id")
    public abstract Intface findById(@Bind("id") long id);

    @SqlQuery("select * from intface where name = :name")
    public abstract Intface findByName(@Bind("name") String name);

    public void initSystemData() {
        if (findByName("SystemDBInterface") == null) {
            Intface intface = new Intface(0, "SystemDBInterface", "System pre-defined sample interface to access a database", "DBInterface", "NA", null, null);
            insert(intface);
        }
    }
}
