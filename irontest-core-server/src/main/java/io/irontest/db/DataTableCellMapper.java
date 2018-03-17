package io.irontest.db;

import io.irontest.models.DataTableCell;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 16/03/2018.
 */
public class DataTableCellMapper implements ResultSetMapper<DataTableCell> {
    public DataTableCell map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        DataTableCell result = new DataTableCell();
        result.setRowSequence(rs.getShort("row_sequence"));
        result.setValue(rs.getString("value"));
        result.setEndpointId(rs.getLong("endpoint_id"));
        return result;
    }
}
