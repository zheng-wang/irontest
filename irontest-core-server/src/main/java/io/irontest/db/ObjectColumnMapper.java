package io.irontest.db;

import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectColumnMapper implements ColumnMapper<Object> {
    @Override
    public Object map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return r.getBytes(columnNumber);
    }
}
