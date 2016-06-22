package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.Endpoint;
import io.irontest.models.ManagedFile;
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
        teststep.setCreated(fields.contains("created") ? rs.getTimestamp("created") : null);
        teststep.setUpdated(fields.contains("updated") ? rs.getTimestamp("updated") : null);
        teststep.setRequest(fields.contains("request") ? rs.getString("request") : null);
        if (fields.contains("endpoint_id")) {
            Endpoint endpoint = new Endpoint();
            endpoint.setId(rs.getLong("endpoint_id"));
            teststep.setEndpoint(endpoint);
        }
        if (fields.contains("request_file_id")) {
            Long requestFileId = rs.getLong("request_file_id");
            if (!rs.wasNull()) {
                teststep.setRequestFile(new ManagedFile(requestFileId));
            }
        }

        return teststep;
    }
}
