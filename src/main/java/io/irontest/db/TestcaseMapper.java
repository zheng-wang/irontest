package io.irontest.db;

import io.irontest.models.Environment;
import io.irontest.models.Testcase;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 1/07/2015.
 */
public class TestcaseMapper implements ResultSetMapper<Testcase> {
    public Testcase map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Testcase testcase = new Testcase(rs.getLong("id"), rs.getString("name"), rs.getString("description"),
                rs.getLong("environmentId"), rs.getTimestamp("created"), rs.getTimestamp("updated"));

        Environment environment = new Environment();
        environment.setName(rs.getString("environmentname"));

        testcase.setEnvironment(environment);

        return testcase;
    }
}
