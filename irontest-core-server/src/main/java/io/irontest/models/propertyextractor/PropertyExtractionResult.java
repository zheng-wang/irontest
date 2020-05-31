package io.irontest.models.propertyextractor;

public class PropertyExtractionResult {
    private String propertyValue;
    private String error;            //  Message of error occurred during extraction. When this field is not null, the propertyValue field should be ignored.

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
