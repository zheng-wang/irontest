package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.assertion.Assertion;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@UseStringTemplate3StatementLocator
@RegisterMapper(AssertionMapper.class)
public abstract class AssertionDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS assertion_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS assertion (" +
            "id BIGINT DEFAULT assertion_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL, type VARCHAR(20) NOT NULL, other_properties CLOB NOT NULL," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT ASSERTION_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) values " +
            "(:a.teststepId, :a.name, :a.type, :otherProperties)")
    @GetGeneratedKeys
    protected abstract long _insert(@BindBean("a") Assertion assertion, @Bind("otherProperties") String otherProperties);

    public long insert_NoTransaction(Assertion assertion) throws JsonProcessingException {
        return _insert(assertion, new ObjectMapper().writeValueAsString(assertion.getOtherProperties()));
    }

    @SqlUpdate("update assertion set name = :name, other_properties = :otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _update(@Bind("name") String name, @Bind("otherProperties") String otherProperties, @Bind("id") long id);

    public int update_NoTransaction(Assertion assertion) throws JsonProcessingException {
        return _update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getOtherProperties()),
                assertion.getId());
    }

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    public abstract List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    public abstract Assertion findById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    /**
     *
     * @param teststepId
     * @param ids can not be empty, otherwise jdbi will throw exception, though H2 works with empty.
     */
    @SqlUpdate("delete from assertion where teststep_id = :teststepId and id not in (<ids>)")
    public abstract void deleteByTeststepIdIfIdNotIn(@Bind("teststepId") long teststepId, @BindIn("ids") List<Long> ids);
}
