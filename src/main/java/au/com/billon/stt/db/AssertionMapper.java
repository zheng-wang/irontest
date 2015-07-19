package au.com.billon.stt.db;

import au.com.billon.stt.models.Assertion;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionMapper implements ResultSetMapper<Assertion> {
    public Assertion map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Assertion assertion = new Assertion(rs.getLong("id"), rs.getLong("teststep_id"), rs.getString("name"),
                rs.getString("type"), rs.getTimestamp("created"), rs.getTimestamp("updated"));
        return assertion;
    }
}