package io.irontest.upgrade;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.irontest.IronTestConfiguration;
import io.irontest.Version;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;

public class UpgradeCommand extends ConfiguredCommand<IronTestConfiguration> {

    public UpgradeCommand() {
        super("upgrade", "Upgrade Iron Test");
    }

    @Override
    protected void run(Bootstrap bootstrap, Namespace namespace, IronTestConfiguration configuration) throws IOException {
        String systemDBVersionStr = getSystemDBVersionStr(configuration);
        String jarFileVersionStr = Version.VERSION;
        DefaultArtifactVersion jarFileVersion = new DefaultArtifactVersion(jarFileVersionStr);
        DefaultArtifactVersion systemDBVersion = new DefaultArtifactVersion(systemDBVersionStr);
        int result = systemDBVersion.compareTo(jarFileVersion);
        if ("SNAPSHOT".equals(systemDBVersion.getQualifier())) {
            System.out.println("System database version " + systemDBVersionStr + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if ("SNAPSHOT".equals(jarFileVersion.getQualifier())) {
            System.out.println("Jar file version " + jarFileVersionStr + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if (result == 0) {
            System.out.println("System database and the jar file are of the same version, so no need to upgrade.");
        } else if (result > 0) {    //  system database version is bigger
            System.out.println("The system database version " + systemDBVersionStr + " is bigger than the jar file version. Please");
            System.out.println("  download and build the latest version of Iron Test,");
            System.out.println("  copy the jar file from the dist folder to your current <IronTest_Home> directory, and");
            System.out.println("  start the new version of Iron Test in your current <IronTest_Home> (like by running the start.bat).");
        } else {    //  system database version is smaller
            UpgradeActions upgradeActions = new UpgradeActions();
            upgradeActions.upgrade(systemDBVersion, jarFileVersion, configuration);
        }
    }

    private String getSystemDBVersionStr(IronTestConfiguration configuration) {
        DataSourceFactory systemDBConfiguration = configuration.getSystemDatabase();
        String systemDBURL = systemDBConfiguration.getUrl();
        String systemDBBaseURL = systemDBURL.split(";")[0];
        Jdbi jdbi = Jdbi.create(systemDBBaseURL + ";IFEXISTS=TRUE",
                systemDBConfiguration.getUser(), systemDBConfiguration.getPassword());
        String systemDBVersionStr = jdbi.withHandle(handle ->
                handle.createQuery("select version from version")
                        .mapTo(String.class)
                        .findOnly());
        return systemDBVersionStr;
    }
}
