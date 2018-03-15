package io.irontest.db;

import io.irontest.models.UserDefinedProperty;
import io.irontest.models.teststep.Teststep;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 29/08/2017.
 */
@RegisterMapper(UserDefinedPropertyMapper.class)
public abstract class UserDefinedPropertyDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS udp_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS udp (" +
            "id BIGINT DEFAULT udp_sequence.NEXTVAL PRIMARY KEY, testcase_id BIGINT, sequence SMALLINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL DEFAULT 'P' || DATEDIFF('MS', '1970-01-01', CURRENT_TIMESTAMP), " +
            "value CLOB NOT NULL DEFAULT '', created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT UDP_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT UDP_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT UDP_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" + CUSTOM_PROPERTY_NAME_CHECK + "))")
    public abstract void createTableIfNotExists();

    /**
     * Unlike {@link TeststepDAO#_insert(Teststep, Object, String, Long, String)}, this method does not consider UDP
     * insertion from test case duplicating. It is already considered with the
     * {@link #duplicateByTestcase(long, long)} method.
     * @param testcaseId
     * @return
     */
    @SqlUpdate("insert into udp (testcase_id, sequence) values (:testcaseId, (" +
            "select coalesce(max(sequence), 0) + 1 from udp where testcase_id = :testcaseId))")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcaseId") long testcaseId);

    @SqlUpdate("update udp set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    public UserDefinedProperty insert(long testcaseId) {
        long id = _insert(testcaseId);
        String name = "P" + id;
        updateNameForInsert(id, name);
        return findById(id);
    }

    @SqlQuery("select * from udp where id = :id")
    protected abstract UserDefinedProperty findById(@Bind("id") long id);

    @SqlQuery("select * from udp where testcase_id = :testcaseId order by sequence")
    public abstract List<UserDefinedProperty> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select u.* from udp u, teststep t where t.id = :teststepId and t.testcase_id = u.testcase_id")
    public abstract List<UserDefinedProperty> findTestcaseUDPsByTeststepId(@Bind("teststepId") long teststepId);

    @SqlUpdate("update udp set name = :name, value = :value, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract void update(@BindBean UserDefinedProperty udp);

    @SqlUpdate("delete from udp where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    /**
     * Copy user defined properties from source test case to target test case.
     * @param sourceTestcaseId
     * @param targetTestcaseId
     */
    @SqlUpdate("insert into UDP (sequence, name, value, testcase_id) select sequence, name, value, :targetTestcaseId from UDP where testcase_id = :sourceTestcaseId")
    public abstract void duplicateByTestcase(@Bind("sourceTestcaseId") long sourceTestcaseId,
                                             @Bind("targetTestcaseId") long targetTestcaseId);

    @SqlQuery("select * from udp where testcase_id = :testcaseId and sequence = :sequence")
    protected abstract UserDefinedProperty findBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlUpdate("update udp set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract void updateSequenceById(@Bind("id") long id, @Bind("newSequence") short newSequence);

    @SqlUpdate("update udp set sequence = case when :direction = 'up' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    protected abstract int batchMove(@Bind("testcaseId") long testcaseId,
                                     @Bind("firstSequence") short firstSequence,
                                     @Bind("lastSequence") short lastSequence,
                                     @Bind("direction") String direction);

    @Transaction
    public void moveInTestcase(long testcaseId, short fromSequence, short toSequence) {
        if (fromSequence != toSequence) {
            long draggedUDPId = findBySequence(testcaseId, fromSequence).getId();

            //  shelve the dragged UDP first
            updateSequenceById(draggedUDPId, (short) -1);

            if (fromSequence < toSequence) {
                batchMove(testcaseId, (short) (fromSequence + 1), toSequence, "up");
            } else {
                batchMove(testcaseId, toSequence, (short) (fromSequence - 1), "down");
            }

            //  move the dragged UDP last
            updateSequenceById(draggedUDPId, toSequence);
        }
    }
}