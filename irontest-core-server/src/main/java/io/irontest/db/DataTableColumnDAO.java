package io.irontest.db;

import io.irontest.models.DataTableColumn;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.*;

@RegisterRowMapper(DataTableColumnMapper.class)
public interface DataTableColumnDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS datatable_column_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS datatable_column (" +
            "id BIGINT DEFAULT datatable_column_sequence.NEXTVAL PRIMARY KEY, " +
            "name VARCHAR(200) NOT NULL DEFAULT 'COL' || DATEDIFF('MS', '1970-01-01', CURRENT_TIMESTAMP), " +
            "type VARCHAR(50) NOT NULL, sequence SMALLINT NOT NULL, testcase_id BIGINT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT DATATABLE_COLUMN_CAPTION_COLUMN_UNRENAMEABLE_CONSTRAINT CHECK(NOT(sequence = 1 AND name <> 'Caption')), " +
            "CONSTRAINT DATATABLE_COLUMN_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" +
                CUSTOM_PROPERTY_NAME_CHECK + "))")
    void createTableIfNotExists();

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) " +
            "select 'Caption', 'String', 1, id from testcase t " +
            "where (select count(*) from datatable_column where testcase_id = t.id) = 0")
    void insertCaptionColumnForTestcasesWithoutDataTableColumn();

    @SqlQuery("select * from datatable_column where testcase_id = :testcaseId order by sequence")
    List<DataTableColumn> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    /**
     * @param testcaseId
     * @param column
     * @param type for enum, name instead of value is bound by JDBI, so use a separate @Bind here instead of taking advantage of the @BindBean.
     * @return
     */
    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) values (:c.name, :type, " +
            ":c.sequence, :testcaseId)")
    @GetGeneratedKeys
    long insert(@Bind("testcaseId") long testcaseId, @BindBean("c") DataTableColumn column,
                @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) values (:name, :type, " +
            "select coalesce(max(sequence), 0) + 1 from datatable_column where testcase_id = :testcaseId, :testcaseId)")
    @GetGeneratedKeys
    long insert(@Bind("testcaseId") long testcaseId, @Bind("name") String name, @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (type, sequence, testcase_id) values (:type, " +
            "select max(sequence) + 1 from datatable_column where testcase_id = :testcaseId, :testcaseId)")
    @GetGeneratedKeys
    long _insert(@Bind("testcaseId") long testcaseId, @Bind("type") String type);

    @SqlUpdate("update datatable_column set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default void insert(long testcaseId, String columnType) {
        long id = _insert(testcaseId, columnType);
        String name = "COL" + id;
        updateNameForInsert(id, name);

        dataTableCellDAO().insertCellsForNewColumn(testcaseId, id);
    }

    @SqlUpdate("update datatable_column set name = :name, updated = CURRENT_TIMESTAMP where id = :id")
    void rename(@Bind("id") long id, @Bind("name") String name);

    @SqlUpdate("delete from datatable_column where id = :id")
    void delete(@Bind("id") long id);

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) " +
            "select name, type, sequence, :targetTestcaseId from datatable_column where testcase_id = :sourceTestcaseId")
    void duplicateByTestcase(@Bind("sourceTestcaseId") long sourceTestcaseId,
                             @Bind("targetTestcaseId") long targetTestcaseId);

    @SqlQuery("select * from datatable_column where testcase_id = :testcaseId and sequence = :sequence")
    DataTableColumn findBySequence(@Bind("testcaseId") long testcaseId, @Bind("sequence") short sequence);

    @SqlUpdate("update datatable_column set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :id")
    void updateSequenceById(@Bind("id") long id, @Bind("newSequence") short newSequence);

    @SqlUpdate("update datatable_column set " +
            "sequence = case when :direction = 'left' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where testcase_id = :testcaseId and sequence >= :firstSequence and sequence <= :lastSequence")
    void batchMove(@Bind("testcaseId") long testcaseId,
                   @Bind("firstSequence") short firstSequence,
                   @Bind("lastSequence") short lastSequence,
                   @Bind("direction") String direction);

    @Transaction
    default void moveInTestcase(long testcaseId, short fromSequence, short toSequence) {
        if (fromSequence != toSequence) {
            long draggedColumnId = findBySequence(testcaseId, fromSequence).getId();

            //  shelve the dragged column first
            updateSequenceById(draggedColumnId, (short) -1);

            if (fromSequence < toSequence) {
                batchMove(testcaseId, (short) (fromSequence + 1), toSequence, "left");
            } else {
                batchMove(testcaseId, toSequence, (short) (fromSequence - 1), "right");
            }

            //  move the dragged column last
            updateSequenceById(draggedColumnId, toSequence);
        }
    }
}