package io.irontest.db;

import io.irontest.models.Testcase;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseMapper implements ResultSetMapper<Testcase> {
    public Testcase map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Testcase testcase = new Testcase(rs.getLong("id"), rs.getString("name"), rs.getString("description"),
                rs.getLong("parent_folder_id"));

        return testcase;
    }
}
