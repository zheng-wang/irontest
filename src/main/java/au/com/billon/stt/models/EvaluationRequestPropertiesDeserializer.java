package au.com.billon.stt.models;

import au.com.billon.stt.utils.STTUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Zheng on 1/08/2015.
 */
public class EvaluationRequestPropertiesDeserializer extends JsonDeserializer<Properties> {
    @Override
    public Properties deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        EvaluationRequest evaluationRequest = (EvaluationRequest) jsonParser.getCurrentValue();
        Properties properties = (Properties) new ObjectMapper().treeToValue(
                node, STTUtils.getEvaluationRequestPropertiesClassByType(evaluationRequest.getType()));
        return properties;
    }
}
