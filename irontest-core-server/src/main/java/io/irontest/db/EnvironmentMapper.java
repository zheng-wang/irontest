package io.irontest.db;

import io.irontest.models.Environment;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnvironmentMapper implements RowMapper<Environment> {
    public Environment map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Environment(rs.getLong("id"), rs.getString("name"), rs.getString("description"), null);
    }
}
