package io.irontest.upgrade;

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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class UpgradeActions {
    private static Logger LOGGER = Logger.getLogger("Upgrade");

    protected void upgrade(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                           String ironTestHome, String fullyQualifiedSystemDBURL, String user, String password)
            throws Exception {
        System.out.println("Upgrading Iron Test from v" + systemDatabaseVersion + " to v" + jarFileVersion + ".");

        Path upgradeWorkspace = Files.createTempDirectory("irontest-upgrade-");
        Path logFilePath = Paths.get(upgradeWorkspace.toString(),
                "upgrade-from-v" + systemDatabaseVersion + "-to-v" + jarFileVersion + ".log");
        FileHandler logFileHandler = new FileHandler(logFilePath.toString());
        logFileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(logFileHandler);
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.info("Created temp upgrade directory " + upgradeWorkspace.toString());

        List<ResourceFile> applicableSystemDBUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion, "db", "SystemDB", "sql");
        boolean needsSystemDBUpgrade = !applicableSystemDBUpgrades.isEmpty();
        if (needsSystemDBUpgrade) {
            System.out.println("Please manually backup <IronTest_Home>/database folder to your normal maintenance backup location. Type y and then Enter to confirm backup completion.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed system database backup completion.");
        }

        //  do upgrade in the 'new' folder under the temp upgrade directory
        if (needsSystemDBUpgrade) {
            Path oldDir = Paths.get(upgradeWorkspace.toString(), "old");
            Path newDir = Paths.get(upgradeWorkspace.toString(), "new");
            Files.createDirectory(oldDir);
            Files.createDirectory(newDir);

            if (needsSystemDBUpgrade) {
                upgradeSystemDB(ironTestHome, fullyQualifiedSystemDBURL, user, password, applicableSystemDBUpgrades,
                        oldDir, newDir, jarFileVersion);
            }

//            //  copy files from the 'new' folder to <IronTest_Home>
//            if (needsSystemDBUpgrade) {
//                String systemDBFileName = getSystemDBFileName(configuration.getSystemDatabase());
//                Path newDatabaseFolder = Paths.get(newDir.toString(), "database");
//                Path ironTestHomeSystemDatabaseFolder = Paths.get(".", "database");
//                Path sourceFile = Paths.get(newDatabaseFolder.toString(), systemDBFileName);
//                Path targetFile = Paths.get(ironTestHomeSystemDatabaseFolder.toString(), systemDBFileName);
//
//                Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
//                LOGGER.info("Copied " + systemDBFileName + " from " + newDatabaseFolder + " to " +
//                        ironTestHomeSystemDatabaseFolder + ".");
//            }
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

    private void upgradeSystemDB(String ironTestHome, String fullyQualifiedSystemDBURL, String user, String password,
                                 List<ResourceFile> applicableSystemDBUpgrades, Path oldDir, Path newDir,
                                 DefaultArtifactVersion jarFileVersion)
            throws IOException {
        Path oldDatabaseFolder = Files.createDirectory(Paths.get(oldDir.toString(), "database"));
        Path newDatabaseFolder = Files.createDirectory(Paths.get(newDir.toString(), "database"));

        String systemDBFileName = getSystemDBFileName(fullyQualifiedSystemDBURL);

        System.out.println(systemDBFileName);

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
