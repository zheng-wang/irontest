package io.irontest;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.roskart.dropwizard.jaxws.EndpointBuilder;
import com.roskart.dropwizard.jaxws.JAXWSBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.irontest.auth.AuthResponseFilter;
import io.irontest.auth.ResourceAuthenticator;
import io.irontest.auth.ResourceAuthorizer;
import io.irontest.auth.SimplePrincipal;
import io.irontest.common.Constants;
import io.irontest.common.Utils;
import io.irontest.db.*;
import io.irontest.models.AppInfo;
import io.irontest.models.AppMode;
import io.irontest.resources.*;
import io.irontest.utils.IronTestUtils;
import io.irontest.ws.ArticleSOAP;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jdbi.v3.core.Jdbi;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class IronTestApplication extends Application<IronTestConfiguration> {
    private JAXWSBundle jaxWsBundle = new JAXWSBundle();

    public static void main(String[] args) throws Exception {
        new IronTestApplication().run(args);
    }

    @Override
    public String getName() {
        return "Iron Test";
    }

    @Override
    public void initialize(Bootstrap<IronTestConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/ui", "index.htm", "ui"));
        bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars", "/ui/lib", null, "lib"));
        bootstrap.addBundle(new AssetsBundle("/assets/mockserver", "/ui/mockserver", "mockserver.htm", "mockserver"));
        bootstrap.addBundle(new AssetsBundle("/assets/common", "/ui/common", null, "common"));
        bootstrap.addBundle(jaxWsBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new ViewBundle<IronTestConfiguration>(){
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(IronTestConfiguration config) {
                return config.getViewRendererConfiguration();
            }
        });
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });

        //  configure the Jackson ObjectMapper used by JAX-RS (Jersey)
        ObjectMapper objectMapper = bootstrap.getObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        IronTestUtils.addMixInsForWireMock(objectMapper);
    }

    private boolean isInTeamMode(IronTestConfiguration configuration) {
        return AppMode.TEAM.toString().equals(configuration.getMode());
    }

    @Override
    public void run(IronTestConfiguration configuration, Environment environment) throws IOException {
        final JdbiFactory jdbiFactory = new JdbiFactory();
        final Jdbi systemDBJdbi = jdbiFactory.build(environment, configuration.getSystemDatabase(), "systemDatabase");

        //  compare system database version with uber jar version to see whether an upgrade is needed
        Integer versionTableCount = systemDBJdbi.withHandle(handle ->
                handle.createQuery("select count(*) from information_schema.tables where table_name = 'VERSION'")
                      .mapTo(Integer.class).findOnly());
        if (versionTableCount == 1) {      //  VERSION table exists in the system database (i.e. we are not starting a brand new Iron Test build)
            if (!checkVersion(systemDBJdbi)) {
                System.out.println("Press Enter to exit.");
                System.in.read();
                System.exit(0);
            }
        }

        //  Override Java's trusted cacerts with our own trust store if available.
        //  Notice that setting the properties without the trust store being existing could cause unexpected result
        //  at runtime with Java 10 (Java 1.8 does not have the issue), such as failure of SOAP test step run (caused
        //  by 'new SSLContextBuilder().loadTrustMaterial').
        if (new File(configuration.getSslTrustStorePath()).exists()) {
            System.setProperty("javax.net.ssl.trustStore", configuration.getSslTrustStorePath());
            System.setProperty("javax.net.ssl.trustStorePassword", configuration.getSslTrustStorePassword());
        }

        //  start WireMock server (in the same JVM)
        WireMockServer wireMockServer = new WireMockServer(options()
                .extensions(new ResponseTemplateTransformer(true))
                .port(Integer.parseInt(configuration.getWireMock().get("port")))
                .maxRequestJournalEntries(Integer.parseInt(configuration.getWireMock().get("maxRequestJournalEntries")))
                .notifier(new WireMockFileNotifier())
        );
        wireMockServer.start();

        createSystemResources(configuration, environment, systemDBJdbi, wireMockServer);
        createSampleResources(configuration, environment);

        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Iron Test started with UI address http://localhost:" +  + getLocalPort(server) + "/ui");
                System.out.println();
            }
        });
    }

    /**
     * @param systemDBJdbi
     * @return true if version check finds no problem, false otherwise.
     */
    private boolean checkVersion(Jdbi systemDBJdbi) {
        DefaultArtifactVersion systemDBVersion = Utils.getSystemDBVersion(systemDBJdbi);
        DefaultArtifactVersion jarFileVersion = new DefaultArtifactVersion(Version.VERSION);
        int comparison = systemDBVersion.compareTo(jarFileVersion);
        if ("SNAPSHOT".equals(systemDBVersion.getQualifier()) || "SNAPSHOT".equals(jarFileVersion.getQualifier())) {
            return true;               //  SNAPSHOT jar or system DB is not considered for upgrade
        } else if (comparison == 0) {  //  system database and the jar file are of the same version
            return true;
        } else if (comparison > 0) {    //  system database version is bigger than the jar file version
            System.out.printf(Constants.PROMPT_TEXT_WHEN_SYSTEM_DB_VERSION_IS_BIGGER_THAN_JAR_VERSION,
                    systemDBVersion, jarFileVersion);
            System.out.println();
            return false;
        } else {    //  system database version is smaller than the jar file version
            System.out.printf("System database version %1$s is smaller than jar file version %2$s.%n", systemDBVersion,
                    jarFileVersion);
            System.out.println("Please download and build the latest release of Iron Test. Under the dist directory, " +
                    "run command 'java -jar <jarFileName> upgrade <IronTest_Home>' to upgrade your existing " +
                    "Iron Test instance.");
            System.out.println("Follow the instructions to finish upgrade. In the end, you should see in the " +
                    "command line output 'UPGRADE SUCCESS'.");
            return false;
        }
    }

    private void createSystemResources(IronTestConfiguration configuration, Environment environment, Jdbi systemDBJdbi,
                                       WireMockServer wireMockServer) {
        systemDBJdbi.registerArgument(new PropertiesArgumentFactory());
        systemDBJdbi.registerArgument(new EndpointPropertiesArgumentFactory());

        //  create DAO objects
        final VersionDAO versionDAO = systemDBJdbi.onDemand(VersionDAO.class);
        final FolderDAO folderDAO = systemDBJdbi.onDemand(FolderDAO.class);
        final EnvironmentDAO environmentDAO = systemDBJdbi.onDemand(EnvironmentDAO.class);
        final EndpointDAO endpointDAO = systemDBJdbi.onDemand(EndpointDAO.class);
        final TestcaseDAO testcaseDAO = systemDBJdbi.onDemand(TestcaseDAO.class);
        final TeststepDAO teststepDAO = systemDBJdbi.onDemand(TeststepDAO.class);
        final AssertionDAO assertionDAO = systemDBJdbi.onDemand(AssertionDAO.class);
        final PropertyExtractorDAO propertyExtractorDAO = systemDBJdbi.onDemand(PropertyExtractorDAO.class);
        final UtilsDAO utilsDAO = systemDBJdbi.onDemand(UtilsDAO.class);
        final FolderTreeNodeDAO folderTreeNodeDAO = systemDBJdbi.onDemand(FolderTreeNodeDAO.class);
        final UserDefinedPropertyDAO udpDAO = systemDBJdbi.onDemand(UserDefinedPropertyDAO.class);
        final DataTableDAO dataTableDAO = systemDBJdbi.onDemand(DataTableDAO.class);
        final DataTableColumnDAO dataTableColumnDAO = systemDBJdbi.onDemand(DataTableColumnDAO.class);
        final DataTableCellDAO dataTableCellDAO = systemDBJdbi.onDemand(DataTableCellDAO.class);
        final TestcaseRunDAO testcaseRunDAO = systemDBJdbi.onDemand(TestcaseRunDAO.class);
        final TestcaseIndividualRunDAO testcaseIndividualRunDAO = systemDBJdbi.onDemand(TestcaseIndividualRunDAO.class);
        final TeststepRunDAO teststepRunDAO = systemDBJdbi.onDemand(TeststepRunDAO.class);
        final HTTPStubMappingDAO httpStubMappingDAO = systemDBJdbi.onDemand(HTTPStubMappingDAO.class);
        UserDAO userDAO = null;
        if (isInTeamMode(configuration)) {
            userDAO = systemDBJdbi.onDemand(UserDAO.class);
        }

        AppInfo appInfo = new AppInfo();
        if (isInTeamMode(configuration)) {
            appInfo.setAppMode(AppMode.TEAM);

            // ignore bindHost
            DefaultServerFactory server = (DefaultServerFactory) configuration.getServerFactory();
            List<ConnectorFactory> applicationConnectors = server.getApplicationConnectors();
            HttpConnectorFactory httpConnectorFactory = (HttpConnectorFactory) applicationConnectors.get(0);
            httpConnectorFactory.setBindHost(null);

            //  turn on user authentication and authorization
            environment.jersey().register(new AuthDynamicFeature(
                    new BasicCredentialAuthFilter.Builder<SimplePrincipal>()
                    .setAuthenticator(new ResourceAuthenticator(userDAO))
                    .setAuthorizer(new ResourceAuthorizer()).buildAuthFilter()));
            environment.jersey().register(RolesAllowedDynamicFeature.class);

            environment.jersey().register(new AuthResponseFilter());
        }

        //  create database tables
        //  keep the order!!! (there are foreign keys linking some of them)
        versionDAO.createTableIfNotExists();
        versionDAO.insertVersionIfNotExists();
        folderDAO.createSequenceIfNotExists();
        folderDAO.createTableIfNotExists();
        folderDAO.insertARootNodeIfNotExists();
        environmentDAO.createSequenceIfNotExists();
        environmentDAO.createTableIfNotExists();
        endpointDAO.createSequenceIfNotExists();
        endpointDAO.createTableIfNotExists();
        testcaseDAO.createSequenceIfNotExists();
        testcaseDAO.createTableIfNotExists();
        teststepDAO.createSequenceIfNotExists();
        teststepDAO.createTableIfNotExists();
        assertionDAO.createSequenceIfNotExists();
        assertionDAO.createTableIfNotExists();
        propertyExtractorDAO.createSequenceIfNotExists();
        propertyExtractorDAO.createTableIfNotExists();
        udpDAO.createSequenceIfNotExists();
        udpDAO.createTableIfNotExists();
        dataTableColumnDAO.createSequenceIfNotExists();
        dataTableColumnDAO.createTableIfNotExists();
        dataTableColumnDAO.insertCaptionColumnForTestcasesWithoutDataTableColumn();
        dataTableCellDAO.createSequenceIfNotExists();
        dataTableCellDAO.createTableIfNotExists();
        testcaseRunDAO.createSequenceIfNotExists();
        testcaseRunDAO.createTableIfNotExists();
        testcaseIndividualRunDAO.createSequenceIfNotExists();
        testcaseIndividualRunDAO.createTableIfNotExists();
        teststepRunDAO.createSequenceIfNotExists();
        teststepRunDAO.createTableIfNotExists();
        httpStubMappingDAO.createSequenceIfNotExists();
        httpStubMappingDAO.createTableIfNotExists();
        if (isInTeamMode(configuration)) {
            userDAO.createSequenceIfNotExists();
            userDAO.createTableIfNotExists();
            userDAO.insertBuiltinAdminUserIfNotExists();
        }

        //  register APIs
        environment.jersey().register(new SystemResource(appInfo));
        environment.jersey().register(new ManagedEndpointResource(appInfo, endpointDAO));
        environment.jersey().register(new TestcaseResource(testcaseDAO, teststepDAO));
        environment.jersey().register(new FolderResource(folderDAO, testcaseDAO));
        environment.jersey().register(new FolderTreeNodeResource(folderTreeNodeDAO));
        environment.jersey().register(new TeststepResource(appInfo, teststepDAO, udpDAO, utilsDAO, dataTableDAO, assertionDAO));
        environment.jersey().register(new WSDLResource());
        environment.jersey().register(new EnvironmentResource(environmentDAO));
        environment.jersey().register(new TestcaseRunResource(testcaseDAO, utilsDAO, testcaseRunDAO, teststepRunDAO, wireMockServer));
        environment.jersey().register(new AssertionResource(udpDAO, teststepDAO, dataTableDAO, assertionDAO));
        environment.jersey().register(new UDPResource(udpDAO));
        environment.jersey().register(new DataTableResource(dataTableDAO, dataTableColumnDAO, dataTableCellDAO));
        environment.jersey().register(new HTTPStubResource(httpStubMappingDAO, wireMockServer, udpDAO, dataTableDAO));
        environment.jersey().register(new MockServerResource(wireMockServer));
        environment.jersey().register(new PropertyExtractorResource(udpDAO, dataTableDAO, propertyExtractorDAO));
        if (isInTeamMode(configuration)) {
            environment.jersey().register(new UserResource(userDAO));
        }

        //  if turned on in config.yml, register jersey LoggingFilter (used for logging Iron Test resource oriented HTTP API requests and responses)
        DefaultLoggingFactory defaultLoggingFactory = (DefaultLoggingFactory) configuration.getLoggingFactory();
        if (defaultLoggingFactory.getLoggers().containsKey(LoggingFilter.class.getName())) {
            environment.jersey().register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true));
        }

        //  register exception mappers
        environment.jersey().register(new IronTestLoggingExceptionMapper());
    }

    private void createSampleResources(IronTestConfiguration configuration, Environment environment) {
        final JdbiFactory jdbiFactory = new JdbiFactory();
        final Jdbi jdbi = jdbiFactory.build(environment, configuration.getSampleDatabase(), "sampleDatabase");

        //  create DAO objects
        final ArticleDAO articleDAO = jdbi.onDemand(ArticleDAO.class);

        //  create database tables
        articleDAO.createTableIfNotExists();
        articleDAO.insertArticle1IfNotExists();

        //  register APIs
        environment.jersey().register(new ArticleResource(articleDAO));

        //  register SOAP web services
        jaxWsBundle.publishEndpoint(new EndpointBuilder("/article", new ArticleSOAP(articleDAO)));
    }
}
