package io.irontest.db;

import io.irontest.models.Environment;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class EnvironmentMapper implements ResultSetMapper<Environment> {
    public Environment map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Environment(rs.getLong("id"), rs.getString("name"), rs.getString("description"), null,
                rs.getTimestamp("updated"));
    }
}
