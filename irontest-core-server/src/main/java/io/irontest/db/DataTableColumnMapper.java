package io.irontest.db;

import io.irontest.models.DataTableColumn;
import io.irontest.models.DataTableColumnType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataTableColumnMapper implements ResultSetMapper<DataTableColumn> {
    public DataTableColumn map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        DataTableColumn result = new DataTableColumn();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setType(DataTableColumnType.getByText(rs.getString("type")));
        return result;
    }
}
