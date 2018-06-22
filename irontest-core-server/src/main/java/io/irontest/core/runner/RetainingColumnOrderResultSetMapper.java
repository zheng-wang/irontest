package io.irontest.core.runner;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keep the entries of the returned Map<String, Object> in the same order as the column names obtained from JDBC ResultSet metadata.
 * Can't return ResultSetMetaData as it is inaccessible after ResultSet.close() (at least for H2).
 */
public class RetainingColumnOrderResultSetMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> map(ResultSet rs, StatementContext ctx) throws SQLException {
        Map<String, Object> orderedRow = new LinkedHashMap<String, Object>();
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String columnName = resultSetMetaData.getColumnLabel(i).toLowerCase();
                orderedRow.put(columnName, rs.getObject(columnName));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to access JDBC ResultSet MetaData.", e);
        }

        return orderedRow;
    }
}
