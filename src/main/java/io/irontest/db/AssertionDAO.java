package io.irontest.db;

import io.irontest.models.assertion.Assertion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 19/07/2015.
 */
@RegisterMapper(AssertionMapper.class)
public abstract class AssertionDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS assertion_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS assertion (" +
            "id BIGINT DEFAULT assertion_sequence.NEXTVAL PRIMARY KEY, teststep_id INT, name VARCHAR(200) NOT NULL, " +
            "type VARCHAR(20) NOT NULL, other_properties CLOB NOT NULL," +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT ASSERTION_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) values " +
            "(:teststepId, :name, :type, :otherProperties)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("teststepId") long teststepId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("otherProperties") String otherProperties);

    public long insert(Assertion assertion) throws JsonProcessingException {
        return insert(assertion.getTeststepId(), assertion.getName(), assertion.getType(),
                new ObjectMapper().writeValueAsString(assertion.getOtherProperties()));
    }

    @SqlUpdate("update assertion set name = :name, other_properties = :otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@Bind("name") String name, @Bind("otherProperties") String otherProperties, @Bind("id") long id);

    public int update(Assertion assertion) throws JsonProcessingException {
        return update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getOtherProperties()),
                assertion.getId());
    }

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    public abstract List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    public abstract Assertion findById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where id = :id")
    public abstract void deleteById(@Bind("id") long id);
}
