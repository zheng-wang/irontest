package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Zheng on 23/07/2015.
 */
public class TeststepInvocationRequestPropertiesDeserializer
        extends JsonDeserializer<TeststepInvocationRequestProperties> {

    @Override
    public TeststepInvocationRequestProperties deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        TeststepInvocationRequestProperties properties = null;
        TeststepInvocationRequest invocationRequest = (TeststepInvocationRequest) jsonParser.getCurrentValue();
        if (TeststepInvocationRequest.TESTSTEP_INVOCATION_TYPE_SOAP.equals(invocationRequest.getType())) {
            properties =  new ObjectMapper().treeToValue(node, SOAPTeststepInvocationRequestProperties.class);
        }

        return properties;
    }
}
