package io.irontest;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Zheng on 20/06/2015.
 */
public class IronTestConfiguration extends Configuration {
    private String mode;

    @Valid
    @NotNull
    private DataSourceFactory systemDatabase = new DataSourceFactory();

    private DataSourceFactory sampleDatabase = new DataSourceFactory();

    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    public String getMode() {
        return mode;
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
}
