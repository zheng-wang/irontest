package io.irontest.upgrade;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.irontest.IronTestConfiguration;
import io.irontest.Version;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import java.util.Scanner;

public class UpgradeCommand extends ConfiguredCommand<IronTestConfiguration> {

    public UpgradeCommand() {
        super("upgrade", "Upgrade Iron Test");
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, IronTestConfiguration configuration) {
//        String systemDatabaseVersionStr = getSystemDatabaseVersionStr(configuration);
//        String jarFileVersionStr = Version.VERSION;
        String systemDatabaseVersionStr = "0.13.0";
        String jarFileVersionStr = "0.15.0";
        DefaultArtifactVersion jarFileVersion = new DefaultArtifactVersion(jarFileVersionStr);
        DefaultArtifactVersion systemDatabaseVersion = new DefaultArtifactVersion(systemDatabaseVersionStr);
        int result = systemDatabaseVersion.compareTo(jarFileVersion);
        if ("SNAPSHOT".equals(systemDatabaseVersion.getQualifier())) {
            System.out.println("System database version " + systemDatabaseVersionStr + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if ("SNAPSHOT".equals(jarFileVersion.getQualifier())) {
            System.out.println("Jar file version " + jarFileVersionStr + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if (result == 0) {
            System.out.println("System database and the jar file are of the same version, so no need to upgrade.");
        } else if (result > 0) {    //  system database version is bigger
            System.out.println("The system database version " + systemDatabaseVersionStr + " is bigger than the jar file version. Please");
            System.out.println("  download and build the latest version of Iron Test,");
            System.out.println("  copy the jar file from the dist folder to your current <IronTest_Home> directory, and");
            System.out.println("  start the new version of Iron Test in your current <IronTest_Home> (like by running the start.bat).");
        } else {    //  system database version is smaller
            UpgradeActions upgradeActions = new UpgradeActions();
            if (upgradeActions.needsSystemDatabaseUpgrade(systemDatabaseVersion, jarFileVersion)) {
                System.out.println("Please manually backup <IronTest_Home>/database folder to your normal maintenance backup location. Type yes and then Enter to confirm backup completion.");
                Scanner scanner = new Scanner(System.in);
                String line = null;
                while (!"yes".equalsIgnoreCase(line)) {
                    line = scanner.nextLine().trim();
                }
            }
        }
    }

    private String getSystemDatabaseVersionStr(IronTestConfiguration configuration) {
        DataSourceFactory systemDatabaseConfiguration = configuration.getSystemDatabase();
        String systemDatabaseURL = systemDatabaseConfiguration.getUrl();
        String systemDatabaseBaseURL = systemDatabaseURL.split(";")[0];
        Jdbi jdbi = Jdbi.create(systemDatabaseBaseURL + ";IFEXISTS=TRUE",
                systemDatabaseConfiguration.getUser(), systemDatabaseConfiguration.getPassword());
        String systemDatabaseVersionStr = jdbi.withHandle(handle ->
                handle.createQuery("select version from version")
                        .mapTo(String.class)
                        .findOnly());
        return systemDatabaseVersionStr;
    }
}
