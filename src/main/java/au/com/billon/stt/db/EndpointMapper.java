package au.com.billon.stt.db;

import au.com.billon.stt.models.Endpoint;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class EndpointMapper implements ResultSetMapper<Endpoint> {
    public Endpoint map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Endpoint(rs.getLong("id"), rs.getString("name"), rs.getString("description"),
            rs.getString("host"), rs.getInt("port"), rs.getString("protocol"), rs.getString("ctxroot"),
            rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}
