package io.irontest.core.propertyextractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.models.propertyextractor.PropertyExtractor;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class PropertyExtractorRunnerFactory {
    private static PropertyExtractorRunnerFactory instance = new PropertyExtractorRunnerFactory();

    private PropertyExtractorRunnerFactory() { }

    public static PropertyExtractorRunnerFactory getInstance() {
        return instance;
    }

    /**
     * This method modifies content of the propertyExtractor object.
     * @param propertyExtractor
     * @param referenceableStringProperties
     * @return
     * @throws IOException
     */
    public PropertyExtractorRunner create(PropertyExtractor propertyExtractor, Map<String, String> referenceableStringProperties) throws IOException {
        PropertyExtractorRunner result;
        String propertyExtractorType = propertyExtractor.getType();
        switch (propertyExtractorType) {
            case PropertyExtractor.TYPE_JSONPATH:
                result = new JSONPathPropertyExtractorRunner();
                break;
            case PropertyExtractor.TYPE_COOKIE:
                result = new CookiePropertyExtractorRunner();
                break;
            default:
                throw new RuntimeException("Unrecognized property extractor type " + propertyExtractorType);
        }

        MapValueLookup stringPropertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);

        //  resolve string property references in propertyExtractor.otherProperties
        ObjectMapper objectMapper = new ObjectMapper();
        String otherPropertiesJSON =  objectMapper.writeValueAsString(propertyExtractor.getOtherProperties());
        String resolvedOtherPropertiesJSON = new StrSubstitutor(stringPropertyReferenceResolver)
                .replace(otherPropertiesJSON);
        Set<String> undefinedStringProperties = stringPropertyReferenceResolver.getUnfoundKeys();
        String tempPropertyExtractorJSON = "{\"type\":\"" + propertyExtractor.getType() + "\",\"otherProperties\":" +
                resolvedOtherPropertiesJSON + "}";
        PropertyExtractor tempPropertyExtractor = objectMapper.readValue(tempPropertyExtractorJSON, PropertyExtractor.class);
        propertyExtractor.setOtherProperties(tempPropertyExtractor.getOtherProperties());

        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }

        result.setPropertyExtractor(propertyExtractor);

        return result;
    }
}
