package io.irontest.db;

import io.irontest.models.Endpoint;
import io.irontest.models.EnvEntry;
import io.irontest.models.Environment;
import io.irontest.models.Intface;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class EnvEntryMapper implements ResultSetMapper<EnvEntry> {
    public EnvEntry map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        EnvEntry enventry = new EnvEntry(rs.getLong("id"), rs.getLong("environmentId"), rs.getLong("intfaceId"), rs.getLong("endpointId"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));

        Environment environment = new Environment();
        environment.setName(rs.getString("environmentname"));
        environment.setDescription(rs.getString("environmentdesc"));

        Intface intface = new Intface();
        intface.setName(rs.getString("intfacename"));
        intface.setDescription(rs.getString("intfacedesc"));

        Endpoint endpoint = new Endpoint();
        endpoint.setName(rs.getString("endpointname"));
        endpoint.setDescription(rs.getString("endpointdesc"));

        enventry.setEnvironment(environment);
        enventry.setIntface(intface);
        enventry.setEndpoint(endpoint);

        return enventry;
    }
}