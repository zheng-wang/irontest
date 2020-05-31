package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.assertion.Assertion;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssertionMapper implements RowMapper<Assertion> {
    public Assertion map(ResultSet rs, StatementContext ctx) throws SQLException {
        Assertion assertion;
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