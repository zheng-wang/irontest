package au.com.billon.stt.models;

import au.com.billon.stt.STTUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionPropertiesDeserializer extends JsonDeserializer<AssertionProperties> {

    @Override
    public AssertionProperties deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Assertion assertion = (Assertion) jsonParser.getCurrentValue();
        AssertionProperties properties = (AssertionProperties) new ObjectMapper().treeToValue(
                node, STTUtils.getAssertionPropertiesClassByType(assertion.getType()));
        return properties;
    }
}
