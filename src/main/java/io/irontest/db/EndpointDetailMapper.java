package io.irontest.db;

import io.irontest.models.EndpointDetail;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 12/07/2015.
 */
public class EndpointDetailMapper implements ResultSetMapper<EndpointDetail> {
    public EndpointDetail map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new EndpointDetail(rs.getLong("id"), rs.getLong("endpointId"), rs.getString("name"), rs.getString("value"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}
