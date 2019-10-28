package io.irontest.db;

import io.irontest.models.DataTableColumn;
import io.irontest.models.DataTableColumnType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataTableColumnMapper implements RowMapper<DataTableColumn> {
    public DataTableColumn map(ResultSet rs, StatementContext ctx) throws SQLException {
        DataTableColumn result = new DataTableColumn();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setType(DataTableColumnType.getByText(rs.getString("type")));
        result.setSequence(rs.getShort("sequence"));
        return result;
    }
}
