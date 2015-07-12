package au.com.billon.stt.db;

import au.com.billon.stt.models.TeststepProperty;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
@RegisterMapper(TeststepPropertyMapper.class)
public interface TeststepPropertyDAO {
    @SqlUpdate("create table IF NOT EXISTS teststep_property (id INT PRIMARY KEY auto_increment, teststep_id INT, " +
            "name varchar(200), value clob, created timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into teststep_property (teststep_id, name, value) values (:teststepId, :name, :value)")
    @GetGeneratedKeys
    long insert(@BindBean TeststepProperty teststepProperty);

    @SqlUpdate("update teststep_property set name = :name, value = :value, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean TeststepProperty teststepProperty);

    @SqlUpdate("update teststep_property set value = :value, updated = CURRENT_TIMESTAMP where teststep_id = :teststepId and name = :name")
    int updateByTeststepIdAndPropertyName(@BindBean TeststepProperty teststepProperty);

    @SqlQuery("select * from teststep_property where teststep_id = :teststepId")
    List<TeststepProperty> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from teststep_property where teststep_id = :teststepId and name = :name")
    TeststepProperty findByTeststepIdAndPropertyName(@Bind("teststepId") long teststepId,
                                                     @Bind("name") String propertyName);
}
