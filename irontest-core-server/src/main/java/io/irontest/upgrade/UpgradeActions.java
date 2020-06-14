package io.irontest.upgrade;

import io.dropwizard.db.DataSourceFactory;
import io.irontest.IronTestConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class UpgradeActions {
    private static Logger LOGGER = Logger.getLogger("Upgrade");

    protected void upgrade(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                           IronTestConfiguration configuration) throws IOException {
        System.out.println("Upgrading Iron Test from v" + systemDatabaseVersion + " to v" + jarFileVersion + ".");

        List<UpgradeResourceFile> applicableSystemDBUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion, "db", "SystemDB", "sql");
        boolean needsSystemDBUpgrade = !applicableSystemDBUpgrades.isEmpty();
        if (needsSystemDBUpgrade) {
            System.out.println("Please manually backup <IronTest_Home>/database folder to your normal maintenance backup location. Type y and then Enter to confirm backup completion.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
        }

        List<UpgradeResourceFile> applicableConfigYmlUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion,
                        "configyml", "ConfigYml", "class");
        boolean needsConfigYmlUpgrade = !applicableConfigYmlUpgrades.isEmpty();
        if (needsConfigYmlUpgrade) {
            System.out.println("Please manually backup <IronTest_Home>/config.yml file to your normal maintenance backup location. Type y and then Enter to confirm backup completion.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
        }

        Path upgradeWorkspace = Files.createTempDirectory("irontest-upgrade-");
        Path logFilePath = Paths.get(upgradeWorkspace.toString(),
                "upgrade-from-v" + systemDatabaseVersion + "-to-v" + jarFileVersion + ".log");
        FileHandler logFileHandler = new FileHandler(logFilePath.toString());
        logFileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(logFileHandler);
        LOGGER.info("Created temp upgrade directory " + upgradeWorkspace.toString());

        if (needsSystemDBUpgrade || needsConfigYmlUpgrade) {
            Path oldDir = Paths.get(upgradeWorkspace.toString(), "old");
            Path newDir = Paths.get(upgradeWorkspace.toString(), "new");
            Files.createDirectory(oldDir);
            Files.createDirectory(newDir);

            if (needsSystemDBUpgrade) {
                upgradeSystemDB(configuration.getSystemDatabase(), applicableSystemDBUpgrades, oldDir, newDir,
                        jarFileVersion);
            }
        }
    }

    /**
     * Result is sorted by fromVersion.
     * @param oldVersion
     * @param newVersion
     * @param subPackage
     * @param prefix
     * @param extension
     * @return
     */
    private List<UpgradeResourceFile> getApplicableUpgradeResourceFiles(DefaultArtifactVersion oldVersion,
                                                      DefaultArtifactVersion newVersion, String subPackage,
                                                      String prefix, String extension) {
        List<UpgradeResourceFile> result = new ArrayList<>();

        Reflections reflections = new Reflections(
                getClass().getPackage().getName() + "." + subPackage, new ResourcesScanner());
        Set<String> upgradeFilePaths =
                reflections.getResources(Pattern.compile(prefix + ".*\\." + extension));
        for (String upgradeFilePath: upgradeFilePaths) {
            String[] upgradeFilePathFragments = upgradeFilePath.split("/");
            String upgradeFileName = upgradeFilePathFragments[upgradeFilePathFragments.length - 1];
            String[] versionsInUpgradeFileName = upgradeFileName.replace(prefix + "_", "").
                    replace("." + extension, "").split("_To_");
            DefaultArtifactVersion fromVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[0].replace("_", "."));
            DefaultArtifactVersion toVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[1].replace("_", "."));
            if (fromVersionInUpgradeFileName.compareTo(oldVersion) >= 0 &&
                    toVersionInUpgradeFileName.compareTo(newVersion) <=0) {
                UpgradeResourceFile upgradeResourceFile = new UpgradeResourceFile();
                upgradeResourceFile.setResourcePath(upgradeFilePath);
                upgradeResourceFile.setFromVersion(fromVersionInUpgradeFileName);
                upgradeResourceFile.setToVersion(toVersionInUpgradeFileName);
                result.add(upgradeResourceFile);
            }
        }

        Collections.sort(result);

        return result;
    }

    private void upgradeSystemDB(DataSourceFactory systemDBConfiguration,
                                 List<UpgradeResourceFile> applicableSystemDBUpgrades, Path oldDir, Path newDir,
                                 DefaultArtifactVersion jarFileVersion)
            throws IOException {
        Path oldDatabaseFolder = Files.createDirectory(Paths.get(oldDir.toString(), "database"));
        Path newDatabaseFolder = Files.createDirectory(Paths.get(newDir.toString(), "database"));
        String systemDBURL = systemDBConfiguration.getUrl();
        String systemDBBaseURL = systemDBURL.split(";")[0];

        //  copy system database to the old and new folders under the temp workspace
        String systemDBRelativePath = systemDBBaseURL.replace("jdbc:h2:", "");
        String[] systemDBFileRelativePathFragments = systemDBRelativePath.split("/");
        String systemDBFileName = systemDBFileRelativePathFragments[systemDBFileRelativePathFragments.length - 1] + ".mv.db";
        Path sourceFile = Paths.get("database", systemDBFileName);
        Path targetOldFile = Paths.get(oldDatabaseFolder.toString(), systemDBFileName);
        Path targetNewFile = Paths.get(newDatabaseFolder.toString(), systemDBFileName);
        Files.copy(sourceFile, targetOldFile);
        LOGGER.info("Copied current system database to " + oldDatabaseFolder.toString());
        Files.copy(sourceFile, targetNewFile);
        LOGGER.info("Copied current system database to " + newDatabaseFolder.toString());

        String newSystemDBURL = "jdbc:h2:" + targetNewFile.toString().replace(".mv.db", "") + ";IFEXISTS=TRUE";
        Jdbi jdbi = Jdbi.create(newSystemDBURL,
                systemDBConfiguration.getUser(), systemDBConfiguration.getPassword());

        //  run SQL scripts against the system database in the 'new' folder
        for (UpgradeResourceFile sqlFile: applicableSystemDBUpgrades) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(sqlFile.getResourcePath())) {
                String sqlScript = IOUtils.toString(is, StandardCharsets.UTF_8.name());
                jdbi.withHandle(handle -> handle.createScript(sqlScript).execute());
            }
            LOGGER.info("Executed SQL script " + sqlFile.getResourcePath() + " in " + newSystemDBURL + ".");
        }

        //  update Version table
        jdbi.withHandle(handle -> handle
                .createUpdate("update version set version = ?, updated = CURRENT_TIMESTAMP")
                .bind(0, jarFileVersion.toString())
                .execute());
        LOGGER.info("Updated Version to " + jarFileVersion + " in " + newSystemDBURL + ".");
    }
}
