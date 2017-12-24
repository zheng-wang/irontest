package io.irontest.db;

import io.irontest.models.User;
import io.irontest.utils.IronTestUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zheng on 24/12/2017.
 */
public class UserMapper implements ResultSetMapper<User> {
    @Override
    public User map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);

        User user = new User(rs.getString("username"));
        user.setId(rs.getLong("id"));
        user.setPassword(fields.contains("password") ? rs.getString("password") : null);
        user.setSalt(fields.contains("salt") ? rs.getString("salt") : null);

        return user;
    }
}