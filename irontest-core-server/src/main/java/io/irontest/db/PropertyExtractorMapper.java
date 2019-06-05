package io.irontest.db;

import io.irontest.core.propertyextractor.JSONPathPropertyExtractor;
import io.irontest.models.teststep.PropertyExtractor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyExtractorMapper implements RowMapper<PropertyExtractor> {
    public PropertyExtractor map(ResultSet rs, StatementContext ctx) throws SQLException {
        PropertyExtractor propertyExtractor = null;
        String propertyExtractorType = rs.getString("type");
        if (PropertyExtractor.TYPE_JSONPATH.equals(propertyExtractorType)) {
            propertyExtractor = new JSONPathPropertyExtractor(rs.getLong("id"), rs.getString("property_name"), rs.getString("type"), rs.getString("path"));
        }

        return propertyExtractor;
    }
}
