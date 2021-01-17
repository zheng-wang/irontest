package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Environment;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.utils.IronTestUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EndpointMapper implements RowMapper<Endpoint> {
    public Endpoint map(ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);

        Endpoint endpoint;
        String type = rs.getString("type");
        if (fields.contains("other_properties") && rs.getString("other_properties") != null) {
            String tempEndpointJSON = "{\"type\":\"" + type + "\",\"otherProperties\":" +
                    rs.getString("other_properties") + "}";
            try {
                endpoint = new ObjectMapper().readValue(tempEndpointJSON, Endpoint.class);
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize other_properties JSON.", e);
            }
        } else {
            endpoint = new Endpoint();
        }

        endpoint.setId(rs.getLong("id"));
        endpoint.setName(rs.getString("name"));
        endpoint.setType(type);
        endpoint.setDescription(rs.getString("description"));
        endpoint.setUrl(fields.contains("url") ? rs.getString("url") : null);
        endpoint.setHost(fields.contains("host") ? rs.getString("host") : null);
        endpoint.setPort(fields.contains("port") ? (Integer) rs.getObject("port") : null);
        endpoint.setUsername(fields.contains("username") ? rs.getString("username") : null);
        endpoint.setPassword(fields.contains("password") ? rs.getString("password") : null);
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
