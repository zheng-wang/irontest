package io.irontest.db;

import io.irontest.models.DataTableCell;
import io.irontest.models.endpoint.Endpoint;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataTableCellMapper implements RowMapper<DataTableCell> {
    public DataTableCell map(ResultSet rs, StatementContext ctx) throws SQLException {
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
