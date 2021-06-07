package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.HashMap;
import java.util.Map;

public class ClearBrowserCache {
    //  a map of <fromVersion> -> <toVersion>
    private Map<DefaultArtifactVersion, DefaultArtifactVersion> versionMap = new HashMap();

    public ClearBrowserCache() {
        versionMap.put(new DefaultArtifactVersion("0.12.4"), new DefaultArtifactVersion("0.13.0"));
        versionMap.put(new DefaultArtifactVersion("0.13.0"), new DefaultArtifactVersion("0.14.0"));
        versionMap.put(new DefaultArtifactVersion("0.14.0"), new DefaultArtifactVersion("0.15.0"));
        versionMap.put(new DefaultArtifactVersion("0.15.0"), new DefaultArtifactVersion("0.16.0"));
        versionMap.put(new DefaultArtifactVersion("0.16.0"), new DefaultArtifactVersion("0.16.1"));
        versionMap.put(new DefaultArtifactVersion("0.16.3"), new DefaultArtifactVersion("0.17.0"));
        versionMap.put(new DefaultArtifactVersion("0.17.0"), new DefaultArtifactVersion("0.17.1"));
    }

    public Map<DefaultArtifactVersion, DefaultArtifactVersion> getVersionMap() {
        return versionMap;
    }
}
