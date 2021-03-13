package io.irontest.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UpgradeActions {
    private static final Logger LOGGER = Logger.getLogger("Upgrade");

    protected void upgrade(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                           String ironTestHome, String fullyQualifiedSystemDBURL, String user, String password)
            throws Exception {
        Formatter logFormatter = new LogFormatter();
        LOGGER.getParent().getHandlers()[0].setFormatter(logFormatter);    //  set formatter for console logging
        LOGGER.info("Upgrading Iron Test from v" + systemDatabaseVersion + " to v" + jarFileVersion + ".");

        //  set up temp upgrade directory
        Path upgradeWorkspace = Files.createTempDirectory("irontest-upgrade-");
        Path logFilePath = Paths.get(upgradeWorkspace.toString(),
                "upgrade-from-v" + systemDatabaseVersion + "-to-v" + jarFileVersion + ".log");
        FileHandler logFileHandler = new FileHandler(logFilePath.toString());
        logFileHandler.setFormatter(logFormatter);
        LOGGER.addHandler(logFileHandler);
        LOGGER.info("Created temp upgrade directory " + upgradeWorkspace.toString());
        Path oldFolderInTempUpgradeDir = Paths.get(upgradeWorkspace.toString(), "old");
        Path newFolderInTempUpgradeDir = Paths.get(upgradeWorkspace.toString(), "new");
        Files.createDirectory(oldFolderInTempUpgradeDir);
        Files.createDirectory(newFolderInTempUpgradeDir);

        //  system DB upgrade includes schema change and/or data migration
        boolean needsSystemDBUpgrade = upgradeSystemDBInTempDirIfNeeded(systemDatabaseVersion, jarFileVersion, ironTestHome,
                fullyQualifiedSystemDBURL, user, password, oldFolderInTempUpgradeDir, newFolderInTempUpgradeDir);

        boolean clearBrowserCacheNeeded = clearBrowserCacheIfNeeded(systemDatabaseVersion, jarFileVersion);

        //  ------------------------- below steps will modify files in <IronTest_Home> -------------------------

        copyFilesToBeUpgraded(ironTestHome, systemDatabaseVersion, jarFileVersion);

        deleteOldJarsFromIronTestHome(ironTestHome);

        copyNewJarFromDistToIronTestHome(jarFileVersion, ironTestHome);

        //  request user to execute pre system database change (upgrade, or simply version update) general manual upgrades if needed
        preSystemDBChangeGeneralManualUpgrades(systemDatabaseVersion, jarFileVersion);

        if (needsSystemDBUpgrade) {            //  copy files from the temp 'new' folder to <IronTest_Home>
            String systemDBFileName = getSystemDBFileName(fullyQualifiedSystemDBURL);
            Path ironTestHomeSystemDatabaseFolder = Paths.get(ironTestHome, "database");
            Path sourceFilePath = Paths.get(newFolderInTempUpgradeDir.toString(), "database", systemDBFileName);
            Path targetFilePath = Paths.get(ironTestHomeSystemDatabaseFolder.toString(), systemDBFileName);
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Copied " + sourceFilePath + " to " + targetFilePath + ".");
        } else {    //  only update version of system database under <IronTest_Home>
            Jdbi jdbi = Jdbi.create(fullyQualifiedSystemDBURL, user, password);
            updateVersionTableInSystemDatabase(jdbi, fullyQualifiedSystemDBURL, jarFileVersion);
        }

        String lineDelimiter = "------------------------------------------------------------------------";
        LOGGER.info(lineDelimiter);
        LOGGER.info("UPGRADE SUCCESS");
        LOGGER.info(lineDelimiter);
        LOGGER.info("You can start Iron Test now.");
        if (clearBrowserCacheNeeded) {
            LOGGER.info("If Iron Test page is already open, refresh the page (no need to restart browser).");
        }
        LOGGER.info(lineDelimiter);
        LOGGER.info("Refer to " + logFilePath + " for upgrade logs.");
    }

    private boolean upgradeSystemDBInTempDirIfNeeded(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                                            String ironTestHome, String fullyQualifiedSystemDBURL, String user, String password,
                                            Path oldFolderInTempUpgradeDir, Path newFolderInTempUpgradeDir) throws IOException {
        List<ResourceFile> applicableSystemDBUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion, "db", "SystemDB", "sql");
        boolean needsSystemDBUpgrade = !applicableSystemDBUpgrades.isEmpty();
        if (needsSystemDBUpgrade) {
            LOGGER.info("Please manually backup <IronTest_Home>/database folder to your normal maintenance backup location. To confirm backup completion, type y and then Enter.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed system database backup completion.");

            upgradeSystemDBInTempDir(ironTestHome, fullyQualifiedSystemDBURL, user, password, applicableSystemDBUpgrades,
                    oldFolderInTempUpgradeDir, newFolderInTempUpgradeDir, jarFileVersion);

            return true;
        } else {
            return false;
        }
    }

    private boolean clearBrowserCacheIfNeeded(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        boolean clearBrowserCacheNeeded = false;
        ClearBrowserCache cleanBrowserCache = new ClearBrowserCache();
        Map<DefaultArtifactVersion, DefaultArtifactVersion> versionMap = cleanBrowserCache.getVersionMap();
        for (Map.Entry<DefaultArtifactVersion, DefaultArtifactVersion> entry: versionMap.entrySet()) {
            DefaultArtifactVersion fromVersion = entry.getKey();
            DefaultArtifactVersion toVersion = entry.getValue();
            if (fromVersion.compareTo(oldVersion) >= 0 && toVersion.compareTo(newVersion) <=0) {
                clearBrowserCacheNeeded = true;
                break;
            }
        }
        if (clearBrowserCacheNeeded) {
            LOGGER.info("Please clear browser cached images and files (last hour is enough). To confirm clear completion, type y and then Enter.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed browser cache clear completion.");
        }

        return clearBrowserCacheNeeded;
    }

    private void copyNewJarFromDistToIronTestHome(DefaultArtifactVersion newJarFileVersion, String ironTestHome)
            throws IOException {
        String newJarFileName = "irontest-" + newJarFileVersion + ".jar";
        Path soureFilePath = Paths.get(".", newJarFileName).toAbsolutePath();
        Path targetFilePath = Paths.get(ironTestHome, newJarFileName).toAbsolutePath();
        Files.copy(soureFilePath, targetFilePath);
        LOGGER.info("Copied " + soureFilePath + " to " + targetFilePath + ".");
    }

    private void deleteOldJarsFromIronTestHome(String ironTestHome) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
                Paths.get(ironTestHome), "irontest-*.jar")) {
            dirStream.forEach(filePath -> {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LOGGER.info("Deleted " + filePath + ".");
            });
        }
    }

    private void copyFilesToBeUpgraded(String ironTestHome, DefaultArtifactVersion oldVersion,
                                       DefaultArtifactVersion newVersion) throws IOException {
        List<CopyFilesForOneVersionUpgrade> applicableCopyFiles =
                new CopyFiles().getApplicableCopyFiles(oldVersion, newVersion);
        for (CopyFilesForOneVersionUpgrade filesForOneVersionUpgrade: applicableCopyFiles) {
            Map<String, String> filePathMap = filesForOneVersionUpgrade.getFilePathMap();
            for (Map.Entry<String, String> mapEntry: filePathMap.entrySet()) {
                Path sourceFilePath = Paths.get(".", mapEntry.getKey()).toAbsolutePath();
                Path targetFilePath = Paths.get(ironTestHome, mapEntry.getValue()).toAbsolutePath();
                Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Copied " + sourceFilePath + " to " + targetFilePath + ".");
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
    private List<ResourceFile> getApplicableUpgradeResourceFiles(DefaultArtifactVersion oldVersion,
                                                                 DefaultArtifactVersion newVersion, String subPackage,
                                                                 String prefix, String extension) {
        List<ResourceFile> result = new ArrayList<>();

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
                ResourceFile upgradeResourceFile = new ResourceFile();
                upgradeResourceFile.setResourcePath(upgradeFilePath);
                upgradeResourceFile.setFromVersion(fromVersionInUpgradeFileName);
                upgradeResourceFile.setToVersion(toVersionInUpgradeFileName);
                result.add(upgradeResourceFile);
            }
        }

        Collections.sort(result);

        return result;
    }

    private String getSystemDBFileName(String fullyQualifiedSystemDBURL) {
        String systemDBBaseURL = fullyQualifiedSystemDBURL.split(";")[0];

        //  copy system database to the old and new folders under the temp workspace
        String systemDBPath = systemDBBaseURL.replace("jdbc:h2:", "");
        String[] systemDBFileRelativePathFragments = systemDBPath.split("[/\\\\]");  // split by / and \
        String systemDBFileName = systemDBFileRelativePathFragments[systemDBFileRelativePathFragments.length - 1] + ".mv.db";
        return systemDBFileName;
    }

    private void upgradeSystemDBInTempDir(String ironTestHome, String fullyQualifiedSystemDBURL, String user, String password,
                                 List<ResourceFile> applicableSystemDBUpgrades, Path oldDir, Path newDir,
                                 DefaultArtifactVersion jarFileVersion)
            throws IOException {
        Path oldDatabaseFolder = Files.createDirectory(Paths.get(oldDir.toString(), "database"));
        Path newDatabaseFolder = Files.createDirectory(Paths.get(newDir.toString(), "database"));
        String systemDBFileName = getSystemDBFileName(fullyQualifiedSystemDBURL);

        Path sourceFile = Paths.get(ironTestHome, "database", systemDBFileName);
        Path targetOldFile = Paths.get(oldDatabaseFolder.toString(), systemDBFileName);
        Path targetNewFile = Paths.get(newDatabaseFolder.toString(), systemDBFileName);
        Files.copy(sourceFile, targetOldFile);
        LOGGER.info("Copied current system database to " + oldDatabaseFolder.toString());
        Files.copy(sourceFile, targetNewFile);
        LOGGER.info("Copied current system database to " + newDatabaseFolder.toString());

        String newSystemDBURL = "jdbc:h2:" + targetNewFile.toString().replace(".mv.db", "") + ";IFEXISTS=TRUE";
        Jdbi jdbi = Jdbi.create(newSystemDBURL, user, password);

        //  run SQL scripts against the system database in the 'new' folder
        for (ResourceFile sqlFile: applicableSystemDBUpgrades) {
            String sqlScript = sqlFile.getResourceAsText();
            jdbi.withHandle(handle -> handle.createScript(sqlScript).execute());
            LOGGER.info("Executed SQL script " + sqlFile.getResourcePath() + " in " + newSystemDBURL + ".");
        }

        updateVersionTableInSystemDatabase(jdbi, newSystemDBURL, jarFileVersion);
    }

    private void updateVersionTableInSystemDatabase(Jdbi jdbi, String systemDBURL, DefaultArtifactVersion newVersion) {
        jdbi.withHandle(handle -> handle
                .createUpdate("update version set version = ?, updated = CURRENT_TIMESTAMP")
                .bind(0, newVersion.toString())
                .execute());
        LOGGER.info("Updated Version to " + newVersion + " in " + systemDBURL + ".");
    }

    private void preSystemDBChangeGeneralManualUpgrades(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion) throws IOException {
        List<ResourceFile> applicableGeneralManualUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion, "manual", "GeneralPreSystemDBChange", "txt");
        for (ResourceFile manualStep: applicableGeneralManualUpgrades) {
            LOGGER.info(manualStep.getResourceAsText());    //  display manual step details to user
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed manual step completion.");
        }
    }
}
