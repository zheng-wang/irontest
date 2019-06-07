package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.irontest.core.propertyextractor.JSONPathPropertyExtractor;

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

    public abstract String extract(String propertyExtractionInput) throws Exception;
}
