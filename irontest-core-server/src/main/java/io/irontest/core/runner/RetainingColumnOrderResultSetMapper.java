package io.irontest.core.runner;

import org.skife.jdbi.v2.DefaultMapper;
import org.skife.jdbi.v2.StatementContext;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keep the entries of the returned Map<String, Object> in the same order as the column names obtained from JDBC ResultSet metadata.
 * Can't return ResultSetMetaData as it is inaccessible after ResultSet.close() (at least for H2).
 * Created by Zheng on 17/12/2016.
 */
public class RetainingColumnOrderResultSetMapper extends DefaultMapper {
    @Override
    public Map<String, Object> map(int index, ResultSet r, StatementContext ctx) {
        Map<String, Object> jdbiRow = super.map(index, r, ctx);
        Map<String, Object> orderedRow = new LinkedHashMap<String, Object>();
        try {
            ResultSetMetaData resultSetMetaData = r.getMetaData();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String columnName = resultSetMetaData.getColumnLabel(i).toLowerCase();
                orderedRow.put(columnName, jdbiRow.get(columnName));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to access JDBC ResultSet MetaData.", e);
        }

        return orderedRow;
    }
}
