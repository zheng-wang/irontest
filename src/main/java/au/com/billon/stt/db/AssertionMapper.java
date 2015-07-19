package au.com.billon.stt.db;

import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.Property;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionMapper implements ResultSetMapper<Assertion> {
    public Assertion map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Assertion assertion = new Assertion(rs.getLong("id"), rs.getLong("teststep_id"), rs.getString("name"),
                rs.getString("type"), rs.getTimestamp("created"), rs.getTimestamp("updated"));
        try {
            assertion.setProperties((List<Property>) new ObjectMapper().readValue(
                    rs.getString("properties"), new TypeReference<List<Property>>() {}));
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize properties json into Property list", e);
        }
        return assertion;
    }
}