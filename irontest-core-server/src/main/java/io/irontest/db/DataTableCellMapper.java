package io.irontest.db;

import io.irontest.models.DataTableCell;
import io.irontest.models.endpoint.Endpoint;
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
        result.setId(rs.getLong("id"));
        result.setRowSequence(rs.getShort("row_sequence"));
        result.setValue(rs.getString("value"));
        long endpointId = rs.getLong("endpoint_id");
        if (!rs.wasNull()) {
            result.setEndpoint(new Endpoint());
            result.getEndpoint().setId(endpointId);
        }

        return result;
    }
}
