package io.irontest.db;

import io.irontest.models.TestResult;
import io.irontest.models.testrun.TestcaseIndividualRun;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseIndividualRunMapper implements RowMapper<TestcaseIndividualRun> {
    public TestcaseIndividualRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TestcaseIndividualRun testcaseIndividualRun = new TestcaseIndividualRun();
        testcaseIndividualRun.setId(rs.getLong("id"));
        testcaseIndividualRun.setCaption(rs.getString("caption"));
        testcaseIndividualRun.setStartTime(rs.getTimestamp("starttime"));
        testcaseIndividualRun.setDuration(rs.getLong("duration"));
        testcaseIndividualRun.setResult(TestResult.getByText(rs.getString("result")));
        return testcaseIndividualRun;
    }
}
