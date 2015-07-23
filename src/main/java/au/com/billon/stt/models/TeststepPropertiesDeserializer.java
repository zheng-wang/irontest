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
public class TeststepPropertiesDeserializer extends JsonDeserializer<TeststepProperties> {

    @Override
    public TeststepProperties deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        TeststepProperties properties = null;
        Teststep teststep = (Teststep) jsonParser.getCurrentValue();
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            properties =  new ObjectMapper().treeToValue(node, SOAPTeststepProperties.class);
        }

        return properties;
    }
}
