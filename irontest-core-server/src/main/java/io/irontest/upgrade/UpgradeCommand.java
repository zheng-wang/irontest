package io.irontest.upgrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.cli.Command;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Bootstrap;
import io.irontest.IronTestConfiguration;
import io.irontest.IronTestConstants;
import io.irontest.Version;
import io.irontest.utils.IronTestUtils;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpgradeCommand extends Command {
    public UpgradeCommand() {
        super("upgrade", "Upgrade Iron Test");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("IronTestHome")
                .dest("IronTestHome")
                .type(String.class)
                .required(true)
                .help("Home directory of the Iron Test instance to be upgraded");
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        String ironTestHome = namespace.getString("IronTestHome");
        IronTestConfiguration configuration = getIronTestConfiguration(ironTestHome);
        DataSourceFactory systemDBConfiguration = configuration.getSystemDatabase();
        String fullyQualifiedSystemDBURL = getFullyQualifiedSystemDBURL(ironTestHome, systemDBConfiguration.getUrl());
        DefaultArtifactVersion systemDBVersion = getSystemDBVersionStr(fullyQualifiedSystemDBURL, systemDBConfiguration.getUser(),
                systemDBConfiguration.getPassword());
        DefaultArtifactVersion jarFileVersion = new DefaultArtifactVersion(Version.VERSION);

        int comparison = systemDBVersion.compareTo(jarFileVersion);
        if ("SNAPSHOT".equals(systemDBVersion.getQualifier())) {
            System.out.println("System database version " + systemDBVersion + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if ("SNAPSHOT".equals(jarFileVersion.getQualifier())) {
            System.out.println("Jar file version " + jarFileVersion + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if (comparison == 0) {
            System.out.println("System database and the jar file are of the same version, so no need to upgrade.");
        } else if (comparison > 0) {    //  system database version is bigger
            System.out.printf(IronTestConstants.PROMPT_TEXT_WHEN_SYSTEM_DB_VERSION_IS_BIGGER_THAN_JAR_VERSION,
                    systemDBVersion, jarFileVersion);
        } else {    //  system database version is smaller
            UpgradeActions upgradeActions = new UpgradeActions();
            upgradeActions.upgrade(systemDBVersion, jarFileVersion, ironTestHome, fullyQualifiedSystemDBURL,
                    systemDBConfiguration.getUser(), systemDBConfiguration.getPassword());
        }
    }

    private IronTestConfiguration getIronTestConfiguration(String ironTestHome) throws IOException, ConfigurationException {
        Path configYmlFilePath = Paths.get(ironTestHome, "config.yml");
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        Validator validator = Validators.newValidator();
        YamlConfigurationFactory<IronTestConfiguration> factory =
                new YamlConfigurationFactory<>(IronTestConfiguration.class, validator, objectMapper, "dw");
        IronTestConfiguration configuration = factory.build(configYmlFilePath.toFile());
        return configuration;
    }

    private String getFullyQualifiedSystemDBURL(String ironTestHome, String originalSystemDBURL) {
        String systemDBBaseURL = originalSystemDBURL.split(";")[0];
        String systemDBPath = systemDBBaseURL.replace("jdbc:h2:", "");
        String fullyQualifiedSystemDBPath = systemDBPath;
        if (systemDBPath.startsWith("./")) {
            fullyQualifiedSystemDBPath = Paths.get(ironTestHome, systemDBPath.replace("./", "")).toString();
        }
        String fullyQualifiedSystemDBURL = "jdbc:h2:" + fullyQualifiedSystemDBPath + ";IFEXISTS=TRUE";
        return fullyQualifiedSystemDBURL;
    }

    private DefaultArtifactVersion getSystemDBVersionStr(String fullyQualifiedSystemDBURL, String user, String password) {
        Jdbi jdbi = Jdbi.create(fullyQualifiedSystemDBURL, user, password);
        return IronTestUtils.getSystemDBVersion(jdbi);
    }
}
