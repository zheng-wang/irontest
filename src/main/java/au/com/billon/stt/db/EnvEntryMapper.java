package au.com.billon.stt.db;

import au.com.billon.stt.models.EnvEntry;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class EnvEntryMapper implements ResultSetMapper<EnvEntry> {
    public EnvEntry map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new EnvEntry(rs.getLong("id"), rs.getLong("environmentId"), rs.getLong("intfaceId"), rs.getLong("endpointId"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}