package au.com.billon.stt.db;

import au.com.billon.stt.models.Assertion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 19/07/2015.
 */
@RegisterMapper(AssertionMapper.class)
public abstract class AssertionDAO {
    @SqlUpdate("create table IF NOT EXISTS assertion (" +
            "id INT PRIMARY KEY auto_increment, teststep_id INT, name varchar(200), " +
            "type varchar(20), properties clob," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, properties) values " +
            "(:teststepId, :name, :type, :properties)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("teststepId") long teststepId, @Bind("name") String name,
                @Bind("type") String type, @Bind("properties") String properties);

    public long insert(Assertion assertion) throws JsonProcessingException {
        System.out.println(assertion.getTeststepId());
        return insert(assertion.getTeststepId(), assertion.getName(), assertion.getType(),
                new ObjectMapper().writeValueAsString(assertion.getProperties()));
    }

    @SqlUpdate("update assertion set name = :name, properties = :properties, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@Bind("name") String name, @Bind("properties") String properties, @Bind("id") long id);

    public int update(Assertion assertion) throws JsonProcessingException {
        return update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getProperties()),
                assertion.getId());
    }

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    public abstract List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    public abstract Assertion findById(@Bind("id") long id);
}
