package io.irontest.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.propertyextractor.PropertyExtractor;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyExtractorMapper implements RowMapper<PropertyExtractor> {
    public PropertyExtractor map(ResultSet rs, StatementContext ctx) throws SQLException {
        PropertyExtractor propertyExtractor;
        String type = rs.getString("type");
        if (rs.getString("other_properties") != null) {
            String tempPropertyExtractorJSON = "{\"type\":\"" + type + "\",\"otherProperties\":" +
                    rs.getString("other_properties") + "}";
            try {
                propertyExtractor = new ObjectMapper().readValue(tempPropertyExtractorJSON, PropertyExtractor.class);
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize other_properties JSON.", e);
            }
        } else {
            propertyExtractor = new PropertyExtractor();
        }
        propertyExtractor.setId(rs.getLong("id"));
        propertyExtractor.setPropertyName(rs.getString("property_name"));
        propertyExtractor.setType(type);

        return propertyExtractor;
    }
}
