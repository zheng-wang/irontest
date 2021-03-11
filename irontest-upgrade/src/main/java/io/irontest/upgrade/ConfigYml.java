package io.irontest.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConfigYml {
    private SystemDatabaseYml systemDatabase;

    public SystemDatabaseYml getSystemDatabase() {
        return systemDatabase;
    }

    public void setSystemDatabase(SystemDatabaseYml systemDatabase) {
        this.systemDatabase = systemDatabase;
    }
}
