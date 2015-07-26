package au.com.billon.stt.models;

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
        AssertionProperties properties = null;
        Assertion assertion = (Assertion) jsonParser.getCurrentValue();
        if (Assertion.ASSERTION_TYPE_CONTAINS.equals(assertion.getType())) {
            properties = new ObjectMapper().treeToValue(node, ContainsAssertionProperties.class);
        } else if (Assertion.ASSERTION_TYPE_XPATH.equals(assertion.getType())) {
            properties = new ObjectMapper().treeToValue(node, XPathAssertionProperties.class);
        } else {
            throw new IOException("Unrecognized assertion type " + assertion.getType());
        }

        return properties;
    }
}
