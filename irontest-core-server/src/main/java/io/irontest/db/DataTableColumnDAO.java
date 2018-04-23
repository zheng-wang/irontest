package io.irontest.db;

import io.irontest.models.DataTableColumn;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 14/03/2018.
 */
@RegisterMapper(DataTableColumnMapper.class)
public abstract class DataTableColumnDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS datatable_column_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS datatable_column (" +
            "id BIGINT DEFAULT datatable_column_sequence.NEXTVAL PRIMARY KEY, " +
            "name VARCHAR(200) NOT NULL DEFAULT 'COL' || DATEDIFF('MS', '1970-01-01', CURRENT_TIMESTAMP), " +
            "type VARCHAR(50) NOT NULL, sequence SMALLINT NOT NULL, testcase_id BIGINT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" +
                CUSTOM_PROPERTY_NAME_CHECK + "))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) " +
            "select 'Caption', 'String', 1, id from testcase t " +
            "where (select count(*) from datatable_column where testcase_id = t.id) = 0")
    public abstract void insertCaptionColumnForTestcasesWithoutDataTableColumn();

    @CreateSqlObject
    protected abstract DataTableCellDAO dataTableCellDAO();

    @SqlQuery("select * from datatable_column where testcase_id = :testcaseId order by sequence")
    public abstract List<DataTableColumn> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    /**
     * @param testcaseId
     * @param column
     * @param type for enum, name instead of value is bound by JDBI, so use a separate @Bind here instead of taking advantage of the @BindBean.
     * @return
     */
    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) values (:name, :type, " +
            ":c.sequence, :testcaseId)")
    @GetGeneratedKeys
    public abstract long insert(@Bind("testcaseId") long testcaseId, @BindBean("c") DataTableColumn column,
                                @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (type, sequence, testcase_id) values (:type, " +
            "select max(sequence) + 1 from datatable_column where testcase_id = :testcaseId, :testcaseId)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("testcaseId") long testcaseId, @Bind("type") String type);

    @SqlUpdate("update datatable_column set name = :name where id = :id")
    protected abstract void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    public void insert(long testcaseId, String columnType) {
        long id = _insert(testcaseId, columnType);
        String name = "COL" + id;
        updateNameForInsert(id, name);

        dataTableCellDAO().insertCellsForNewColumn(testcaseId, id);
    }

    @SqlUpdate("update datatable_column set name = :name, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract void rename(@Bind("id") long id, @Bind("name") String name);
}