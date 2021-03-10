package io.irontest.upgrade;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    public String getResourceAsText() throws IOException {
        String result;
        //  read text file from inside a jar file
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            result = IOUtils.toString(is, StandardCharsets.UTF_8.name());
        }
        return result;
    }
}
