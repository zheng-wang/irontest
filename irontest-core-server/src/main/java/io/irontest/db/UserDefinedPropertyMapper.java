package io.irontest.db;

import io.irontest.models.UserDefinedProperty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 30/08/2017.
 */
public class UserDefinedPropertyMapper implements ResultSetMapper<UserDefinedProperty> {
    public UserDefinedProperty map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        UserDefinedProperty udp = new UserDefinedProperty(
                rs.getLong("id"), rs.getShort("sequence"), rs.getString("name"), rs.getString("value"));

        return udp;
    }
}