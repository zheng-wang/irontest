package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.Environment;
import io.irontest.models.Properties;
import io.irontest.utils.IronTestUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class EndpointMapper implements ResultSetMapper<Endpoint> {
    public Endpoint map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);

        String type = rs.getString("type");
        Properties otherProperties = null;
        if (fields.contains("other_properties") && rs.getString("other_properties") != null) {
            try {
                otherProperties = (Properties) new ObjectMapper().readValue(
                        rs.getString("other_properties"), IronTestUtils.getEndpointPropertiesClassByType(type));
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize other_properties JSON.", e);
            }
        }

        Endpoint endpoint = new Endpoint(rs.getLong("id"), null, rs.getString("name"), type,
                rs.getString("description"), fields.contains("url") ? rs.getString("url") : null,
                fields.contains("username") ? rs.getString("username") : null,
                fields.contains("password") ? rs.getString("password") : null, otherProperties,
                fields.contains("created") ? rs.getTimestamp("created") : null,
                fields.contains("updated") ? rs.getTimestamp("updated") : null);

        if (fields.contains("environment_id") && rs.getObject("environment_id") != null) {
            Environment environment = new Environment();
            environment.setId(rs.getLong("environment_id"));
            if (fields.contains("environment_name")) {
                environment.setName(rs.getString("environment_name"));
            }
            endpoint.setEnvironment(environment);
        }

        return endpoint;
    }
}
