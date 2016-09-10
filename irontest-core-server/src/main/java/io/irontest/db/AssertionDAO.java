package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.NamespacePrefix;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.ContainsAssertionProperties;
import io.irontest.models.assertion.XPathAssertionProperties;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.ArrayList;
import java.util.List;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

/**
 * Created by Zheng on 19/07/2015.
 */
@UseStringTemplate3StatementLocator
@RegisterMapper(AssertionMapper.class)
public abstract class AssertionDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS assertion_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS assertion (" +
            "id BIGINT DEFAULT assertion_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT, " +
            "name VARCHAR(200) NOT NULL, type VARCHAR(20) NOT NULL, other_properties CLOB NOT NULL," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT ASSERTION_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) values " +
            "(:teststepId, :name, :type, :otherProperties)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("teststepId") long teststepId, @Bind("name") String name,
                                @Bind("type") String type, @Bind("otherProperties") String otherProperties);

    public long insert(long teststepId, Assertion assertion) throws JsonProcessingException {
        if (Assertion.TYPE_CONTAINS.equals(assertion.getType())) {
            assertion.setOtherProperties(new ContainsAssertionProperties("value"));
        } else if (Assertion.TYPE_XPATH.equals(assertion.getType())) {
            assertion.setOtherProperties(
                    new XPathAssertionProperties("true()", "true", new ArrayList<NamespacePrefix>()));
        }
        return _insert(teststepId, assertion.getName(), assertion.getType(),
                new ObjectMapper().writeValueAsString(assertion.getOtherProperties()));
    }

    @SqlUpdate("update assertion set name = :name, other_properties = :otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _update(@Bind("name") String name, @Bind("otherProperties") String otherProperties, @Bind("id") long id);

    public int update(Assertion assertion) throws JsonProcessingException {
        return _update(assertion.getName(), new ObjectMapper().writeValueAsString(assertion.getOtherProperties()),
                assertion.getId());
    }

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    public abstract List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    public abstract Assertion findById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where teststep_id = :teststepId and id not in (<ids>)")
    public abstract void deleteByTeststepIdIfIdNotIn(@Bind("teststepId") long teststepId, @BindIn("ids") List<Long> ids);
}
