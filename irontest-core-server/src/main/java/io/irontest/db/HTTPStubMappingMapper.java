package io.irontest.db;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HTTPStubMappingMapper implements RowMapper<HTTPStubMapping> {
    @Override
    public HTTPStubMapping map(ResultSet rs, StatementContext ctx) throws SQLException {
        String specJSON = rs.getString("spec_json");
        StubMapping spec = StubMapping.buildFrom(specJSON);
        HTTPStubMapping httpStubMapping = new HTTPStubMapping(
                rs.getLong("id"), rs.getLong("testcase_id"),
                rs.getShort("number"), spec, rs.getString("request_body_main_pattern_value"),
                rs.getShort("expected_hit_count"));

        return httpStubMapping;
    }
}
