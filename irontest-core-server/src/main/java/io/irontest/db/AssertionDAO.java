package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Properties;
import io.irontest.models.assertion.Assertion;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(AssertionMapper.class)
public interface AssertionDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS assertion_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS assertion (" +
            "id BIGINT DEFAULT assertion_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL, type VARCHAR(50) NOT NULL, other_properties CLOB NOT NULL," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT ASSERTION_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) values " +
            "(:a.teststepId, :a.name, :a.type, :otherProperties)")
    @GetGeneratedKeys
    long _insert(@BindBean("a") Assertion assertion, @Bind("otherProperties") String otherProperties);

    default long insert(Assertion assertion) throws JsonProcessingException {
        return _insert(assertion, new ObjectMapper().writeValueAsString(assertion.getOtherProperties()));
    }

    @SqlUpdate("update assertion set name = :name, other_properties = :otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    void _update(@Bind("name") String name, @Bind("otherProperties") String otherProperties, @Bind("id") long id);

    default void update(Assertion assertion) throws JsonProcessingException {
        _update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getOtherProperties()),
                assertion.getId());
    }

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    Assertion findById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where id = :id")
    void deleteById(@Bind("id") long id);

    /**
     *
     * @param teststepId
     * @param ids can not be empty, otherwise jdbi will throw exception, though H2 works with empty.
     */
    @SqlUpdate("delete from assertion where teststep_id = :teststepId and id not in (<ids>)")
    void deleteByTeststepIdIfIdNotIn(@Bind("teststepId") long teststepId, @BindList("ids") List<Long> ids);

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) " +
            "select :newTeststepId, name, type, other_properties from assertion where teststep_id = :oldTeststepId")
    void duplicateByTeststep(@Bind("oldTeststepId") long oldTeststepId, @Bind("newTeststepId") long newTeststepId);

    @SqlUpdate("update assertion set other_properties = :otherProperties, updated = CURRENT_TIMESTAMP where id = :id")
    void _updateOtherProperties(@Bind("otherProperties") String otherProperties, @Bind("id") long id);

    default void updateOtherProperties(long assertionId, Properties properties) throws JsonProcessingException {
        _updateOtherProperties(new ObjectMapper().writeValueAsString(properties), assertionId);
    }
}
