package io.irontest.db;

import io.irontest.models.HTTPStubMapping;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HTTPStubMappingMapper implements RowMapper<HTTPStubMapping> {
    @Override
    public HTTPStubMapping map(ResultSet rs, StatementContext ctx) throws SQLException {
        HTTPStubMapping httpStubMapping = new HTTPStubMapping(rs.getString("spec_json"));

        return httpStubMapping;
    }
}
