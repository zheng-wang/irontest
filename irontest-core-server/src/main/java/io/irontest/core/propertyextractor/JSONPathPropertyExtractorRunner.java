package io.irontest.core.propertyextractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.propertyextractor.JSONPathPropertyExtractorProperties;

public class JSONPathPropertyExtractorRunner extends PropertyExtractorRunner {
    @Override
    public String extract(String input) throws JsonProcessingException {
        JSONPathPropertyExtractorProperties otherProperties =
                (JSONPathPropertyExtractorProperties) getPropertyExtractor().getOtherProperties();

        Object value = JsonPath.read(input, otherProperties.getPath());
        if (value instanceof String) {
            return (String) value;         //  ObjectMapper().writeValueAsString returns the string surrounded with ".
        } else {
            return new ObjectMapper().writeValueAsString(value);
        }
    }
}
