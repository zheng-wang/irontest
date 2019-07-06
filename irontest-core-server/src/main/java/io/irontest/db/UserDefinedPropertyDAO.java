package io.irontest.db;

import io.irontest.models.UserDefinedProperty;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.*;

@RegisterRowMapper(UserDefinedPropertyMapper.class)
public interface UserDefinedPropertyDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS udp_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS udp (" +
            "id BIGINT DEFAULT udp_sequence.NEXTVAL PRIMARY KEY, testcase_id BIGINT, sequence SMALLINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL DEFAULT 'P' || DATEDIFF('MS', '1970-01-01', CURRENT_TIMESTAMP), " +
            "value CLOB NOT NULL DEFAULT '', created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT UDP_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT UDP_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT UDP_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" + CUSTOM_PROPERTY_NAME_CHECK + "))")
    void createTableIfNotExists();

    @SqlUpdate("insert into udp (testcase_id, sequence) values (:testcaseId, (" +
            "select coalesce(max(sequence), 0) + 1 from udp where testcase_id = :testcaseId))")
    @GetGeneratedKeys
    long _insertWithoutName(@Bind("testcaseId") long testcaseId);

    @SqlUpdate("insert into udp (testcase_id, sequence, name, value) values (:testcaseId, (" +
            "select coalesce(max(sequence), 0) + 1 from udp where testcase_id = :testcaseId), :name, :value)")
    @GetGeneratedKeys
    long _insertWithName(@Bind("testcaseId") long testcaseId, @Bind("name") String name, @Bind("value") String value);

    @SqlUpdate("update udp set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default UserDefinedProperty insert(long testcaseId) {
        long id = _insertWithoutName(testcaseId);
        String name = "P" + id;
        updateNameForInsert(id, name);
        return findById(id);
    }

    @SqlQuery("select * from udp where id = :id")
    UserDefinedProperty findById(@Bind("id") long id);

    @SqlQuery("select * from udp where testcase_id = :testcaseId order by sequence")
    List<UserDefinedProperty> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlUpdate("update udp set name = :name, value = :value, updated = CURRENT_TIMESTAMP where id = :id")
    void update(@BindBean UserDefinedProperty udp);

    @SqlUpdate("delete from udp where id = :id")
    void deleteById(@Bind("id") long id);

    /**
     * Copy user defined properties from source test case to target test case.
     * @param sourceTestcaseId
     * @param targetTestcaseId
     */
    @SqlUpdate("insert into UDP (sequence, name, value, testcase_id) select sequence, name, value, :targetTestcaseId from UDP where testcase_id = :sourceTestcaseId")
    void duplicateByTestcase(@Bind("sourceTestcaseId") long sourceTestcaseId,
                             @Bind("targetTestcaseId") long targetTestcaseId);

    @SqlQuery("select * from udp where testcase_id = :testcaseId and sequence = :sequence")
    UserDefinedProperty findBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlUpdate("update udp set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :id")
    void updateSequenceById(@Bind("id") long id, @Bind("newSequence") short newSequence);

    @SqlUpdate("update udp set sequence = case when :direction = 'up' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    void batchMove(@Bind("testcaseId") long testcaseId,
                   @Bind("firstSequence") short firstSequence,
                   @Bind("lastSequence") short lastSequence,
                   @Bind("direction") String direction);

    @Transaction
    default void moveInTestcase(long testcaseId, short fromSequence, short toSequence) {
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