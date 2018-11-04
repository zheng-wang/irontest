package io.irontest;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class IronTestConfiguration extends Configuration {
    private String mode;
    private String sslTrustStorePath;
    private String sslTrustStorePassword;
    @Valid @NotNull
    private DataSourceFactory systemDatabase = new DataSourceFactory();
    private DataSourceFactory sampleDatabase = new DataSourceFactory();
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();
    private Map<String, String> wireMock = Collections.emptyMap();

    public String getMode() {
        return mode;
    }

    public String getSslTrustStorePath() {
        return sslTrustStorePath;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public DataSourceFactory getSystemDatabase() {
        return systemDatabase;
    }

    public void setSystemDatabase(DataSourceFactory systemDatabase) {
        this.systemDatabase = systemDatabase;
    }

    public DataSourceFactory getSampleDatabase() {
        return sampleDatabase;
    }

    public void setSampleDatabase(DataSourceFactory sampleDatabase) {
        this.sampleDatabase = sampleDatabase;
    }

    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }

    public void setViewRendererConfiguration(Map<String, Map<String, String>> viewRendererConfiguration) {
        final ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
        for (Map.Entry<String, Map<String, String>> entry : viewRendererConfiguration.entrySet()) {
            builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
        }
        this.viewRendererConfiguration = builder.build();
    }

    public Map<String, String> getWireMock() {
        return wireMock;
    }

    public void setWireMock(Map<String, String> wireMock) {
        this.wireMock = wireMock;
    }
}
