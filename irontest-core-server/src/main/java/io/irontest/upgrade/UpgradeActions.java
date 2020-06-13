package io.irontest.upgrade;

import io.dropwizard.db.DataSourceFactory;
import io.irontest.IronTestConfiguration;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class UpgradeActions {
    private static Logger LOGGER = Logger.getLogger("Upgrade");

    protected void upgrade(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                           IronTestConfiguration configuration) throws IOException {
        boolean needsSystemDBUpgrade = needsSystemDBUpgrade(systemDatabaseVersion, jarFileVersion);
        if (needsSystemDBUpgrade) {
            System.out.println("Please manually backup <IronTest_Home>/database folder to your normal maintenance backup location. Type y and then Enter to confirm backup completion.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
        }
        boolean needsConfigYmlUpgrade = needsConfigYmlUpgrade(systemDatabaseVersion, jarFileVersion);
        if (needsConfigYmlUpgrade) {
            System.out.println("Please manually backup <IronTest_Home>/config.yml file to your normal maintenance backup location. Type y and then Enter to confirm backup completion.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
        }

        Path upgradeWorkspace = Files.createTempDirectory("irontest-upgrade");
        Path logFilePath = Paths.get(upgradeWorkspace.toString(),
                "upgrade-from-v" + systemDatabaseVersion + "-to-v" + jarFileVersion + ".log");
        FileHandler logFileHandler = new FileHandler(logFilePath.toString());
        logFileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(logFileHandler);
        LOGGER.info("Create temp upgrade directory " + upgradeWorkspace.toString());

        if (needsSystemDBUpgrade || needsConfigYmlUpgrade) {
            Path oldDir = Paths.get(upgradeWorkspace.toString(), "old");
            Path newDir = Paths.get(upgradeWorkspace.toString(), "new");
            Files.createDirectory(oldDir);
            Files.createDirectory(newDir);

            if (needsSystemDBUpgrade) {
                upgradeSystemDB(configuration.getSystemDatabase(), oldDir, newDir);
            }
        }
    }

    private boolean needsSystemDBUpgrade(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        Set<String> systemDBUpgradeFilePaths = getApplicableUpgradeFilePaths(oldVersion, newVersion, "db",
                "SystemDB", "sql");

        return !systemDBUpgradeFilePaths.isEmpty();
    }

    private boolean needsConfigYmlUpgrade(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
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

    private void upgradeSystemDB(DataSourceFactory systemDBConfiguration, Path oldDir, Path newDir) throws IOException {
        Path oldDatabaseFolder = Files.createDirectory(Paths.get(oldDir.toString(), "database"));
        Path newDatabaseFolder = Files.createDirectory(Paths.get(newDir.toString(), "database"));

        String systemDBURL = systemDBConfiguration.getUrl();
        String systemDBBaseURL = systemDBURL.split(";")[0];
        String systemDBRelativePath = systemDBBaseURL.replace("jdbc:h2:", "");
        String[] systemDBFileRelativePathFragments = systemDBRelativePath.split("/");
        String systemDBFileName = systemDBFileRelativePathFragments[systemDBFileRelativePathFragments.length - 1] + ".mv.db";
        Path sourceFile = Paths.get("database", systemDBFileName);
        Path targetOldFile = Paths.get(oldDatabaseFolder.toString(), systemDBFileName);
        Path targetNewFile = Paths.get(newDatabaseFolder.toString(), systemDBFileName);
        LOGGER.info("Copy current system database to " + oldDatabaseFolder.toString());
        Files.copy(sourceFile, targetOldFile);
        LOGGER.info("Copy current system database to " + newDatabaseFolder.toString());
        Files.copy(sourceFile, targetNewFile);
    }
}
