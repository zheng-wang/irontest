package io.irontest.core.propertyextractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.teststep.PropertyExtractionResult;
import io.irontest.models.teststep.PropertyExtractor;

public class JSONPathPropertyExtractor extends PropertyExtractor {
    public JSONPathPropertyExtractor() {}

    public JSONPathPropertyExtractor(long id, String propertyName, String type, String path) {
        super(id, propertyName, type, path);
    }

    @Override
    public PropertyExtractionResult extract(String input) throws JsonProcessingException {
        PropertyExtractionResult result = new PropertyExtractionResult();
        Object value = JsonPath.read(input, getPath());
        result.setPropertyValue(new ObjectMapper().writeValueAsString(value));
        return result;
    }
}
