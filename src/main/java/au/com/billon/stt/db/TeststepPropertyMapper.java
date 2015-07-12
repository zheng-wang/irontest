package au.com.billon.stt.db;

import au.com.billon.stt.models.TeststepProperty;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 12/07/2015.
 */
public class TeststepPropertyMapper implements ResultSetMapper<TeststepProperty> {
    public TeststepProperty map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new TeststepProperty(rs.getLong("id"), rs.getLong("teststep_id"), rs.getString("name"), rs.getString("value"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}
