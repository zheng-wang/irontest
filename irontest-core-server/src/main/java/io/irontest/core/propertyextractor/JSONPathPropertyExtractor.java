package io.irontest.core.propertyextractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.teststep.PropertyExtractor;

public class JSONPathPropertyExtractor extends PropertyExtractor {
    public JSONPathPropertyExtractor() {}

    public JSONPathPropertyExtractor(long id, String propertyName, String type, String path) {
        super(id, propertyName, type, path);
    }

    @Override
    public String extract(String input) throws JsonProcessingException {
        Object value = JsonPath.read(input, getPath());
        if (value instanceof String) {
            return (String) value;         //  ObjectMapper().writeValueAsString returns the string surrounded with ".
        } else {
            return new ObjectMapper().writeValueAsString(value);
        }
    }
}
