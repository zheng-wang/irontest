package au.com.billon.stt.db;

import au.com.billon.stt.models.Intface;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 7/4/15.
 */
@RegisterMapper(IntfaceMapper.class)
public interface IntfaceDAO {
    @SqlUpdate("create table IF NOT EXISTS intface (id INT PRIMARY KEY auto_increment, name varchar(50) UNIQUE not null, description varchar(500), relpath varchar(50), defurl varchar(200)," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into intface (name, description, relpath, defurl) values (:name, :description, :relpath, :defurl)")
    @GetGeneratedKeys
    long insert(@BindBean Intface intface);

    @SqlUpdate("update intface set name = :name, description = :description, relpath = :relpath, defurl = :defurl, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Intface intface);

    @SqlUpdate("delete from intface where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from intface")
    List<Intface> findAll();

    @SqlQuery("select * from intface where id = :id")
    Intface findById(@Bind("id") long id);
}
