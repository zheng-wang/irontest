package io.irontest.db;

import io.irontest.models.Endpoint;
import io.irontest.utils.IronTestUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class EndpointMapper implements ResultSetMapper<Endpoint> {
    public Endpoint map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);
        Endpoint endpoint = new Endpoint(rs.getLong("id"),
                fields.contains("environment_id") && rs.getObject("environment_id") != null ?
                        new Long(rs.getLong("environment_id")) : null,
                rs.getString("name"),
                fields.contains("type") ? rs.getString("type") : null,
                rs.getString("description"),
                fields.contains("url") ? rs.getString("url") : null,
                fields.contains("username") ? rs.getString("username") : null,
                fields.contains("password") ? rs.getString("password") : null,
                fields.contains("created") ? rs.getTimestamp("created") : null,
                fields.contains("updated") ? rs.getTimestamp("updated") : null);

        if (fields.contains("environment_name")) {
            endpoint.setEnvironmentName(rs.getString("environment_name"));
        }

        return endpoint;
    }
}
