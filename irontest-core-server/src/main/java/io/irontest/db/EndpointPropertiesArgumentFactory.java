package io.irontest.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.models.endpoint.EndpointProperties;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class EndpointPropertiesArgumentFactory extends AbstractArgumentFactory<EndpointProperties> {
    public EndpointPropertiesArgumentFactory() {
        super(Types.CLOB);
    }

    @Override
    protected Argument build(EndpointProperties value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            try {
                statement.setString(position, new ObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Fail to serialize the EndpointProperties object.");
            }
        };
    }
}
