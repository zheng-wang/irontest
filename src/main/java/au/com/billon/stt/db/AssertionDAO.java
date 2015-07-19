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
            "(:teststepId, :name, :type, :propertiesString)")
    @GetGeneratedKeys
    long insert(@BindBean Assertion assertion);

    @SqlUpdate("update assertion set name = :name, properties = :propertiesString, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Assertion assertion);

    @SqlQuery("select * from assertion")
    List<Assertion> findAll();

    @SqlQuery("select * from assertion where id = :id")
    Assertion findById(@Bind("id") long id);
}
