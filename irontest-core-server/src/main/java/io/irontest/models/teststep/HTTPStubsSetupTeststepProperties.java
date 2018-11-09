package io.irontest.models.teststep;

import io.irontest.models.HTTPStubMapping;
import io.irontest.models.Properties;

import java.util.List;

public class HTTPStubsSetupTeststepProperties extends Properties {
    private List<HTTPStubMapping> httpStubMappings;

    public List<HTTPStubMapping> getHttpStubMappings() {
        return httpStubMappings;
    }

    public void setHttpStubMappings(List<HTTPStubMapping> httpStubMappings) {
        this.httpStubMappings = httpStubMappings;
    }
}
