package io.irontest.db;

import io.irontest.models.DataTableColumn;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
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
            "id BIGINT DEFAULT datatable_column_sequence.NEXTVAL PRIMARY KEY, name VARCHAR(200) NOT NULL, " +
            "type VARCHAR(50) NOT NULL, sequence SMALLINT NOT NULL, testcase_id BIGINT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" +
                CUSTOM_PROPERTY_NAME_CHECK + "))")
    public abstract void createTableIfNotExists();

    @SqlQuery("select * from datatable_column where testcase_id = :testcaseId order by sequence")
    public abstract List<DataTableColumn> findByTestcaseId(@Bind("testcaseId") long testcaseId);
}