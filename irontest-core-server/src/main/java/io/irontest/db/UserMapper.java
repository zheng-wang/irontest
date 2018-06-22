package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.User;
import io.irontest.utils.IronTestUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public class UserMapper implements RowMapper<User> {
    @Override
    public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);

        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(fields.contains("password") ? rs.getString("password") : null);
        user.setSalt(fields.contains("salt") ? rs.getString("salt") : null);
        if (fields.contains("roles") && rs.getString("roles") != null) {
            try {
                user.getRoles().addAll(new ObjectMapper().readValue(rs.getString("roles"), HashSet.class));
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize roles JSON.", e);
            }
        }

        return user;
    }
}