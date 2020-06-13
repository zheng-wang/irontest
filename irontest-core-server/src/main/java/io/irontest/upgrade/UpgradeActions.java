package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class UpgradeActions {
    public boolean needsSystemDBUpgrade(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        Set<String> systemDBUpgradeFilePaths = getApplicableUpgradeFilePaths(oldVersion, newVersion, "db",
                "SystemDB", "sql");

        return !systemDBUpgradeFilePaths.isEmpty();
    }

    public boolean needsConfigYmlUpgrade(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        Set<String> configYmlUpgradeFilePaths = getApplicableUpgradeFilePaths(oldVersion, newVersion,
                "configyml", "ConfigYml", "class");

        return !configYmlUpgradeFilePaths.isEmpty();
    }

    private Set<String> getApplicableUpgradeFilePaths(DefaultArtifactVersion oldVersion,
                                                      DefaultArtifactVersion newVersion, String subPackage,
                                                      String prefix, String extension) {
        Set<String> result = new HashSet<>();

        Reflections reflections = new Reflections(
                getClass().getPackage().getName() + "." + subPackage, new ResourcesScanner());
        Set<String> upgradeFilePaths =
                reflections.getResources(Pattern.compile(prefix + ".*\\." + extension));
        for (String upgradeFilePath: upgradeFilePaths) {
            String[] upgradeFilePathFragments = upgradeFilePath.split("/");
            String upgradeFileName = upgradeFilePathFragments[upgradeFilePathFragments.length - 1];
            String[] versionsInUpgradeFileName = upgradeFileName.replace(prefix + "_", "").
                    replace("." + extension, "").split("_To_");
            DefaultArtifactVersion oldVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[0].replace("_", "."));
            DefaultArtifactVersion newVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[1].replace("_", "."));
            if (oldVersionInUpgradeFileName.compareTo(oldVersion) >= 0 &&
                    newVersionInUpgradeFileName.compareTo(newVersion) <=0) {
                result.add(upgradeFilePath);
            }
        }

        return result;
    }
}
