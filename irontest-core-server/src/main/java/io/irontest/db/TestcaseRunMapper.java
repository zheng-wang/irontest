package io.irontest.db;

import io.irontest.models.TestResult;
import io.irontest.models.testrun.RegularTestcaseRun;
import io.irontest.models.testrun.TestcaseRun;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseRunMapper implements ResultSetMapper<TestcaseRun> {
    public TestcaseRun map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        TestcaseRun testcaseRun = new RegularTestcaseRun();

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
