package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ResourceFile implements Comparable<ResourceFile> {
    private DefaultArtifactVersion fromVersion;
    private DefaultArtifactVersion toVersion;
    private String resourcePath;

    public DefaultArtifactVersion getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(DefaultArtifactVersion fromVersion) {
        this.fromVersion = fromVersion;
    }

    public DefaultArtifactVersion getToVersion() {
        return toVersion;
    }

    public void setToVersion(DefaultArtifactVersion toVersion) {
        this.toVersion = toVersion;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public int compareTo(ResourceFile o) {
        return this.fromVersion.compareTo(o.fromVersion);
    }

    public String getResourceAsText() {
        return "aaa";
    }
}
