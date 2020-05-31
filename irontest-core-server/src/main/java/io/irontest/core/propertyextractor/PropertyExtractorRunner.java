package io.irontest.core.propertyextractor;

import io.irontest.models.propertyextractor.PropertyExtractor;

public abstract class PropertyExtractorRunner {
    private PropertyExtractor propertyExtractor;

    protected PropertyExtractor getPropertyExtractor() {
        return propertyExtractor;
    }

    protected void setPropertyExtractor(PropertyExtractor propertyExtractor) {
        this.propertyExtractor = propertyExtractor;
    }

    public abstract String extract(String propertyExtractionInput) throws Exception;
}
