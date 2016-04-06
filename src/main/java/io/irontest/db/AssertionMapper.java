package io.irontest.db;

import io.irontest.utils.IronTestUtils;
import io.irontest.models.Assertion;
import io.irontest.models.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionMapper implements ResultSetMapper<Assertion> {
    public Assertion map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        String type = rs.getString("type");
        Properties properties = null;
        try {
            properties = (Properties) new ObjectMapper().readValue(
                    rs.getString("properties"), IronTestUtils.getAssertionPropertiesClassByType(type));
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize properties JSON.", e);
        }

        return new Assertion(rs.getLong("id"), rs.getLong("teststep_id"), rs.getString("name"),
                type, properties, rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}