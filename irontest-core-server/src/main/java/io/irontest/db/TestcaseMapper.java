package io.irontest.db;

import io.irontest.models.Testcase;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestcaseMapper implements RowMapper<Testcase> {
    public Testcase map(ResultSet rs, StatementContext ctx) throws SQLException {
        Testcase testcase = new Testcase(rs.getLong("id"), rs.getString("name"), rs.getString("description"),
                rs.getLong("parent_folder_id"), rs.getBoolean("check_http_stubs_hit_order"));

        return testcase;
    }
}
