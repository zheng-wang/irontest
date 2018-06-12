package io.irontest.db;

import io.irontest.models.TestResult;
import io.irontest.models.testrun.TestcaseIndividualRun;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseIndividualRunMapper implements ResultSetMapper<TestcaseIndividualRun> {
    public TestcaseIndividualRun map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        TestcaseIndividualRun testcaseIndividualRun = new TestcaseIndividualRun();
        testcaseIndividualRun.setId(rs.getLong("id"));
        testcaseIndividualRun.setCaption(rs.getString("caption"));
        testcaseIndividualRun.setStartTime(rs.getTimestamp("starttime"));
        testcaseIndividualRun.setDuration(rs.getLong("duration"));
        testcaseIndividualRun.setResult(TestResult.getByText(rs.getString("result")));
        return testcaseIndividualRun;
    }
}
