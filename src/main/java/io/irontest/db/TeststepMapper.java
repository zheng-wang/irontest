package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.Properties;
import io.irontest.models.Teststep;
import io.irontest.utils.IronTestUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
public class TeststepMapper implements ResultSetMapper<Teststep> {
    public Teststep map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = IronTestUtils.getFieldsPresentInResultSet(rs);
        String type = rs.getString("type");
        Properties properties = null;
        Class propertiesClass = IronTestUtils.getTeststepPropertiesClassByType(type);
        try {
            if (propertiesClass != null && fields.contains("properties")) {
                properties = (Properties) new ObjectMapper().readValue(rs.getString("properties"), propertiesClass);
            }
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize properties JSON.", e);
        }

        Teststep teststep = new Teststep(rs.getLong("id"), rs.getLong("testcase_id"), rs.getShort("sequence"),
                rs.getString("name"), rs.getString("type"), rs.getString("description"), properties,
                fields.contains("created") ? rs.getTimestamp("created") : null,
                fields.contains("updated") ? rs.getTimestamp("updated") : null,
                fields.contains("request") ? rs.getString("request") : null);

        if (fields.contains("endpoint_id")) {
            Endpoint endpoint = new Endpoint();
            endpoint.setId(rs.getLong("endpoint_id"));
            teststep.setEndpoint(endpoint);
        }

        return teststep;
    }
}
