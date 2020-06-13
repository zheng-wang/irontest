package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.Set;
import java.util.regex.Pattern;

public class UpgradeActions {
    public boolean needsSystemDatabaseUpgrade(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        Reflections reflections = new Reflections(getClass().getPackage().getName() + ".db", new ResourcesScanner());
        Set<String> systemDatabaseUpgradeSqlFiles =
                reflections.getResources(Pattern.compile("SystemDB.*\\.sql"));
        for (String sqlFilePath: systemDatabaseUpgradeSqlFiles) {
            String[] sqlFilePathFragments = sqlFilePath.split("/");
            String sqlFileName = sqlFilePathFragments[sqlFilePathFragments.length - 1];
            String[] versionsInSqlFileName = sqlFileName.replace("SystemDB_", "").
                    replace(".sql", "").split("_To_");
            DefaultArtifactVersion oldVersionInSqlFileName = new DefaultArtifactVersion(
                    versionsInSqlFileName[0].replace("_", "."));
            DefaultArtifactVersion newVersionInSqlFileName = new DefaultArtifactVersion(
                    versionsInSqlFileName[1].replace("_", "."));
            if (oldVersionInSqlFileName.compareTo(oldVersion) >= 0 && newVersionInSqlFileName.compareTo(newVersion) <=0) {
                return true;
            }
        }

        return false;
    }
}
