package io.irontest.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.testrun.TeststepRun;
import io.irontest.models.teststep.Teststep;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

public class TeststepRunMapper implements RowMapper<TeststepRun> {
    public TeststepRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeststepRun teststepRun = new TeststepRun();
        ObjectMapper objectMapper = new ObjectMapper();

        teststepRun.setId(rs.getLong("id"));
        teststepRun.setStartTime(rs.getTimestamp("starttime"));
        teststepRun.setDuration(rs.getLong("duration"));
        teststepRun.setResult(TestResult.getByText(rs.getString("result")));

        Teststep teststep = null;
        try {
            teststep = objectMapper.readValue(rs.getString("teststep"), Teststep.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize teststep JSON.", e);
        }
        teststepRun.setTeststep(teststep);

        //  Use LinkedHashMap here instead of Object (for covering specific response type like DBAPIResponse),
        //    because TeststepRun is used for displaying report, so JSON representation of the response is sufficient.
        LinkedHashMap response = null;
        try {
            response = objectMapper.readValue(rs.getString("response"), LinkedHashMap.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize response JSON.", e);
        }
        teststepRun.setResponse(response);
        teststepRun.setInfoMessage(rs.getString("info_message"));
        teststepRun.setErrorMessage(rs.getString("error_message"));

        List<AssertionVerification> assertionVerifications = null;
        try {
            assertionVerifications = new ObjectMapper().readValue(rs.getString("assertion_verifications"),
                    new TypeReference<List<AssertionVerification>>() { });
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize stepruns JSON.", e);
        }
        teststepRun.setAssertionVerifications(assertionVerifications);

        return teststepRun;
    }
}
