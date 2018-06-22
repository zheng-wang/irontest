package io.irontest.db;

import io.irontest.models.TestResult;
import io.irontest.models.testrun.TestcaseRun;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseRunMapper implements RowMapper<TestcaseRun> {
    public TestcaseRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TestcaseRun testcaseRun = new TestcaseRun();

        testcaseRun.setId(rs.getLong("id"));
        testcaseRun.setTestcaseId(rs.getLong("testcase_id"));
        testcaseRun.setTestcaseName(rs.getString("testcase_name"));
        testcaseRun.setTestcaseFolderPath(rs.getString("testcase_folderpath"));
        testcaseRun.setStartTime(rs.getTimestamp("starttime"));
        testcaseRun.setDuration(rs.getLong("duration"));
        testcaseRun.setResult(TestResult.getByText(rs.getString("result")));

        return testcaseRun;
    }
}
