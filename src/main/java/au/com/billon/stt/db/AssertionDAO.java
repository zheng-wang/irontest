package au.com.billon.stt.db;

import au.com.billon.stt.models.Assertion;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 19/07/2015.
 */
@RegisterMapper(AssertionMapper.class)
public interface AssertionDAO {
    @SqlUpdate("create table IF NOT EXISTS assertion (" +
            "id INT PRIMARY KEY auto_increment, teststep_id INT, name varchar(200), " +
            "type varchar(20), properties clob," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, properties) values " +
            "(:teststepId, :name, :type, :properties)")
    @GetGeneratedKeys
    long insert(@Bind("teststepId") long teststepId, @Bind("name") String name,
                @Bind("type") String type, @Bind("properties") String properties);

    @SqlUpdate("update assertion set name = :name, properties = :properties, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@Bind("name") String name, @Bind("properties") String properties, @Bind("id") long id);

    @SqlQuery("select * from assertion")
    List<Assertion> findAll();

    @SqlQuery("select * from assertion where id = :id")
    Assertion findById(@Bind("id") long id);
}
