package io.irontest.common;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

public class Utils {
    public static DefaultArtifactVersion getSystemDBVersion(Jdbi jdbi) {
        String versionStr = jdbi.withHandle(handle ->
                handle.createQuery("select version from version")
                        .mapTo(String.class)
                        .findOnly());
        return new DefaultArtifactVersion(versionStr);
    }
}
