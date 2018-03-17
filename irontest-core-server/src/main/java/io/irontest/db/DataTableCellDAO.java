package io.irontest.db;

import io.irontest.models.DataTableCell;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 14/03/2018.
 */
@RegisterMapper(DataTableCellMapper.class)
public abstract class DataTableCellDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS datatable_cell_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS datatable_cell (" +
            "id BIGINT DEFAULT datatable_cell_sequence.NEXTVAL PRIMARY KEY, column_id BIGINT NOT NULL, " +
            "row_sequence SMALLINT NOT NULL, value CLOB, endpoint_id BIGINT, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (column_id) REFERENCES datatable_column(id), " +
            "FOREIGN KEY (endpoint_id) REFERENCES endpoint(id), " +
            "CONSTRAINT DATATABLE_CELL_EXCLUSIVE_TYPE_CONSTRAINT CHECK(" +
                "(value is null AND endpoint_id is not null) OR (value is not null AND endpoint_id is null)))")
    public abstract void createTableIfNotExists();

    @SqlQuery("select * from datatable_cell where column_id = :columnId order by row_sequence")
    public abstract List<DataTableCell> findByColumnId(@Bind("columnId") long columnId);
}