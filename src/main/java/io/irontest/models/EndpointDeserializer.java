package io.irontest.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Created by zhenw9 on 26/05/2016.
 */
public class EndpointDeserializer extends JsonDeserializer<Endpoint> {
    public Endpoint deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(node);
        System.out.println(node.get("type"));
        return null;
    }
}
