package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionDeserializer extends JsonDeserializer<Assertion> {

    @Override
    public Assertion deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        long id = node.get("id") == null ? 0 : node.get("id").asLong();
        long teststepId = node.get("teststepId") == null ? 0 : node.get("teststepId").asLong();
        String name = node.get("name") == null ? null : node.get("name").textValue();
        String type = node.get("type") == null ? null : node.get("type").textValue();
        Properties properties = null;
        if (node.get("properties") != null) {
            if (Assertion.ASSERTION_TYPE_CONTAINS.equals(type)) {
                properties =  new ObjectMapper().treeToValue(node.get("properties"), ContainsAssertionProperties.class);
            }
        }
        Date created = node.get("created") == null ? null : new Date(node.get("created").asLong());
        Date updated = node.get("updated") == null ? null : new Date(node.get("updated").asLong());

        return new Assertion(id, teststepId, name, type, properties, created, updated);
    }
}
