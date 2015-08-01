package au.com.billon.stt.db;

import au.com.billon.stt.models.*;
import au.com.billon.stt.utils.STTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 11/07/2015.
 */
public class TeststepMapper implements ResultSetMapper<Teststep> {
    public Teststep map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        String type = rs.getString("type");
        Properties properties = null;
        Class propertiesClass = STTUtils.getTeststepPropertiesClassByType(type);
        try {
            if (propertiesClass != null) {
                properties = (Properties) new ObjectMapper().readValue(rs.getString("properties"), propertiesClass);
            }
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize properties JSON.", e);
        }

        Teststep teststep = new Teststep(rs.getLong("id"), rs.getLong("testcase_id"), rs.getString("name"),
                rs.getString("type"), rs.getString("description"), properties, rs.getTimestamp("created"),
                rs.getTimestamp("updated"), rs.getString("request"), rs.getLong("intfaceId"), rs.getLong("endpointId"));

        Intface intface = new Intface();
        intface.setId(rs.getLong("intfaceId"));
        intface.setName(rs.getString("intfaceName"));

        teststep.setIntface(intface);

        Endpoint endpoint = new Endpoint();
        endpoint.setId(rs.getLong("endpointId"));
        endpoint.setName(rs.getString("endpointName"));

        teststep.setEndpoint(endpoint);

        return teststep;
    }
}
