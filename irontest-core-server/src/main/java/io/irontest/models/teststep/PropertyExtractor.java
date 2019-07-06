package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.irontest.core.MapValueLookup;
import io.irontest.core.propertyextractor.JSONPathPropertyExtractor;
import org.apache.commons.text.StrSubstitutor;

import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JSONPathPropertyExtractor.class, name = PropertyExtractor.TYPE_JSONPATH)})
public abstract class PropertyExtractor {
    public static final String TYPE_JSONPATH = "JSONPath";

    private long id;
    private String propertyName;
    private String type;
    private String path;

    public PropertyExtractor() {}

    public PropertyExtractor(long id, String propertyName, String type, String path) {
        this.id = id;
        this.propertyName = propertyName;
        this.type = type;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String extract(String propertyExtractionInput, Map<String, String> referenceableStringProperties) throws Exception {
        MapValueLookup stringPropertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);

        //  resolve string property references in path
        this.path = new StrSubstitutor(stringPropertyReferenceResolver).replace(this.path);
        Set<String> undefinedStringProperties = stringPropertyReferenceResolver.getUnfoundKeys();

        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }

        return _extract(propertyExtractionInput);
    }

    public abstract String _extract(String propertyExtractionInput) throws Exception;
}
