package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Zheng on 21/07/2015.
 */
public class TeststepDeserializer extends JsonDeserializer<Teststep> {

    @Override
    public Teststep deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        long id = node.get("id") == null ? 0 : node.get("id").asLong();
        long testcaseId = node.get("testcaseId") == null ? 0 : node.get("testcaseId").asLong();
        String name = node.get("name") == null ? null : node.get("name").textValue();
        String type = node.get("type") == null ? null : node.get("type").textValue();
        String description = node.get("description") == null ? null : node.get("description").textValue();
        Properties properties = null;
        if (node.get("properties") != null) {
            if (Teststep.TEST_STEP_TYPE_SOAP.equals(type)) {
                properties =  new ObjectMapper().treeToValue(node.get("properties"), SOAPTeststepProperties.class);
            }
        }
        Date created = node.get("created") == null ? null : new Date(node.get("created").asLong());
        Date updated = node.get("updated") == null ? null : new Date(node.get("updated").asLong());
        String request = node.get("request") == null ? null : node.get("request").textValue();
        long intfaceId = node.get("intfaceId") == null ? 0 : node.get("intfaceId").asLong();

        return new Teststep(id, testcaseId, name, type, description, properties, created, updated, request, intfaceId);
    }
}
