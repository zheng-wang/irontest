package au.com.billon.stt.db;

import au.com.billon.stt.models.Intface;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 7/4/15.
 */
public class IntfaceMapper implements ResultSetMapper<Intface> {
    public Intface map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Intface(rs.getLong("id"), rs.getString("name"), rs.getString("description"), rs.getString("relpath"), rs.getString("defurl"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}
