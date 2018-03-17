package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
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

        Teststep teststep = null;
        String type = rs.getString("type");
        if (fields.contains("other_properties") && rs.getString("other_properties") != null) {
            String tempTeststepJSON = "{\"type\":\"" + type + "\",\"otherProperties\":" +
                    rs.getString("other_properties") + "}";
            try {
                teststep = new ObjectMapper().readValue(tempTeststepJSON, Teststep.class);
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize other_properties JSON.", e);
            }
        } else {
            teststep = new Teststep();
        }

        teststep.setId(rs.getLong("id"));
        teststep.setTestcaseId(rs.getLong("testcase_id"));
        teststep.setSequence(rs.getShort("sequence"));
        teststep.setName(rs.getString("name"));
        teststep.setType(type);
        teststep.setDescription(rs.getString("description"));
        teststep.setAction(fields.contains("action") ? rs.getString("action") : null);
        //  this line must go before the 'if (fields.contains("request")) {' block
        teststep.setRequestType(fields.contains("request_type") ?
                TeststepRequestType.getByText(rs.getString("request_type")) : null);
        if (fields.contains("request")) {
            //  no use of retrieving request file here
            Object request = teststep.getRequestType() == TeststepRequestType.FILE ?
                    null : rs.getBytes("request") == null ? null : new String(rs.getBytes("request"));
            teststep.setRequest(request);
        }
        teststep.setRequestFilename(fields.contains("request_filename") ?
                rs.getString("request_filename") : null);
        if (fields.contains("endpoint_id")) {
            Endpoint endpoint = new Endpoint();
            endpoint.setId(rs.getLong("endpoint_id"));
            teststep.setEndpoint(endpoint);
        }
        teststep.setEndpointProperty(fields.contains("endpoint_property") ? rs.getString("endpoint_property") : null);

        return teststep;
    }
}
