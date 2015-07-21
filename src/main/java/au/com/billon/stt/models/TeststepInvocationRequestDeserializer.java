package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Zheng on 21/07/2015.
 */
public class TeststepInvocationRequestDeserializer extends JsonDeserializer<TeststepInvocationRequest> {

    @Override
    public TeststepInvocationRequest deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type") == null ? null : node.get("type").textValue();
        String request = node.get("request") == null ? null : node.get("request").textValue();
        Properties properties = null;
        if (node.get("properties") != null) {
            if (TeststepInvocationRequest.TESTSTEP_INVOCATION_TYPE_SOAP.equals(type)) {
                properties =  new ObjectMapper().treeToValue(node.get("properties"),
                        SOAPTeststepInvocationRequestProperties.class);
            }
        }

        return new TeststepInvocationRequest(type, request, properties);
    }
}
