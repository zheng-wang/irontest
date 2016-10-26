package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.assertion.Assertion;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionMapper implements ResultSetMapper<Assertion> {
    public Assertion map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Assertion assertion = null;
        String type = rs.getString("type");
        String tempAssertionJSON = "{\"type\":\"" + type + "\",\"otherProperties\":" +
                rs.getString("other_properties") + "}";
        try {
            assertion = new ObjectMapper().readValue(tempAssertionJSON, Assertion.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize other_properties JSON.", e);
        }

        assertion.setId(rs.getLong("id"));
        assertion.setTeststepId(rs.getLong("teststep_id"));
        assertion.setName(rs.getString("name"));
        assertion.setType(type);

        return assertion;
    }
}