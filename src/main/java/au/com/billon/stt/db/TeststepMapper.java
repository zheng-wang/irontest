package au.com.billon.stt.db;

import au.com.billon.stt.models.Intface;
import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.models.TeststepProperties;
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
        TeststepProperties properties = null;
        try {
            if (Teststep.TEST_STEP_TYPE_SOAP.equals(type)) {
                properties = new ObjectMapper().readValue(rs.getString("properties"), SOAPTeststepProperties.class);
            }
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize properties JSON.", e);
        }

        Teststep teststep = new Teststep(rs.getLong("id"), rs.getLong("testcase_id"), rs.getString("name"),
                rs.getString("type"), rs.getString("description"), properties, rs.getTimestamp("created"),
                rs.getTimestamp("updated"), rs.getString("request"), rs.getLong("intfaceId"));

        Intface intface = new Intface();
        intface.setName(rs.getString("intfaceName"));

        teststep.setIntface(intface);

        return teststep;
    }
}
