package io.irontest.db;

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
                rs.getTimestamp("created"), rs.getTimestamp("updated"));

        return testcase;
    }
}
