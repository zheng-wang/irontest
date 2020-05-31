package io.irontest.models.propertyextractor;

public class PropertyExtractionRequest {
    private PropertyExtractor propertyExtractor;
    private String input;

    public PropertyExtractor getPropertyExtractor() {
        return propertyExtractor;
    }

    public void setPropertyExtractor(PropertyExtractor propertyExtractor) {
        this.propertyExtractor = propertyExtractor;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
