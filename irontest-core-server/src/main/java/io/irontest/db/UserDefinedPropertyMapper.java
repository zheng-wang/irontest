package io.irontest.db;

import io.irontest.models.UserDefinedProperty;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDefinedPropertyMapper implements RowMapper<UserDefinedProperty> {
    public UserDefinedProperty map(ResultSet rs, StatementContext ctx) throws SQLException {
        UserDefinedProperty udp = new UserDefinedProperty(
                rs.getLong("id"), rs.getShort("sequence"), rs.getString("name"), rs.getString("value"));

        return udp;
    }
}