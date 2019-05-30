package io.irontest.models.teststep;

public class PropertyExtractor {
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
}
