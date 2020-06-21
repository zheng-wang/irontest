package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.HashMap;
import java.util.Map;

public class CopyFilesForOneVersionUpgrade {
    private DefaultArtifactVersion fromVersion;
    private DefaultArtifactVersion toVersion;
    private Map<String, String> filePathMap = new HashMap<>();

    public CopyFilesForOneVersionUpgrade(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    public DefaultArtifactVersion getFromVersion() {
        return fromVersion;
    }

    public DefaultArtifactVersion getToVersion() {
        return toVersion;
    }

    public Map<String, String> getFilePathMap() {
        return filePathMap;
    }
}
