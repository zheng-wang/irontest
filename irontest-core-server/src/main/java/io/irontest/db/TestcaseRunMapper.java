package io.irontest.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.TestResult;
import io.irontest.models.Testcase;
import io.irontest.models.TestcaseRun;
import io.irontest.models.teststep.TeststepRun;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zheng on 2/08/2016.
 */
public class TestcaseRunMapper implements ResultSetMapper<TestcaseRun> {
    public TestcaseRun map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        TestcaseRun testcaseRun = new TestcaseRun();

        testcaseRun.setId(rs.getLong("id"));
        Testcase testcase = new Testcase();
        testcase.setId(rs.getLong("testcase_id"));
        testcase.setName(rs.getString("testcase_name"));
        testcase.setFolderPath(rs.getString("testcase_folderpath"));
        testcaseRun.setTestcase(testcase);
        testcaseRun.setStartTime(rs.getTimestamp("starttime"));
        testcaseRun.setDuration(rs.getLong("duration"));
        testcaseRun.setResult(TestResult.getByText(rs.getString("result")));
        List<TeststepRun> stepRuns = null;
        try {
            stepRuns = new ObjectMapper().readValue(rs.getString("stepruns"), new TypeReference<List<TeststepRun>>() { });
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize stepruns JSON.", e);
        }
        testcaseRun.setStepRuns(stepRuns);

        return testcaseRun;
    }
}
