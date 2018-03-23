package io.irontest;

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
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.logging.DefaultLoggingFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.irontest.auth.AuthResponseFilter;
import io.irontest.auth.ResourceAuthenticator;
import io.irontest.auth.ResourceAuthorizer;
import io.irontest.auth.SimplePrincipal;
import io.irontest.db.*;
import io.irontest.models.AppInfo;
import io.irontest.models.AppMode;
import io.irontest.resources.*;
import io.irontest.ws.ArticleSOAP;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Zheng on 20/06/2015.
 */
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
        bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars", "/ui/lib", null, "uilib"));
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
    }

    private boolean isInTeamMode(IronTestConfiguration configuration) {
        return AppMode.TEAM.toString().equals(configuration.getMode());
    }

    @Override
    public void run(IronTestConfiguration configuration, Environment environment) {
        // Override Java's trusted cacerts with our own trust store.
        System.setProperty("javax.net.ssl.trustStore", configuration.getSslTrustStorePath());
        System.setProperty("javax.net.ssl.trustStorePassword", configuration.getSslTrustStorePassword());

        createSystemResources(configuration, environment);
        createSampleResources(configuration, environment);
    }

    private void createSystemResources(IronTestConfiguration configuration, Environment environment) {
        final DBIFactory dbiFactory = new DBIFactory();
        final DBI jdbi = dbiFactory.build(environment, configuration.getSystemDatabase(), "systemDatabase");

        //  create DAO objects
        final FolderDAO folderDAO = jdbi.onDemand(FolderDAO.class);
        final EnvironmentDAO environmentDAO = jdbi.onDemand(EnvironmentDAO.class);
        final EndpointDAO endpointDAO = jdbi.onDemand(EndpointDAO.class);
        final TestcaseDAO testcaseDAO = jdbi.onDemand(TestcaseDAO.class);
        final TeststepDAO teststepDAO = jdbi.onDemand(TeststepDAO.class);
        final AssertionDAO assertionDAO = jdbi.onDemand(AssertionDAO.class);
        final UtilsDAO utilsDAO = jdbi.onDemand(UtilsDAO.class);
        final FolderTreeNodeDAO folderTreeNodeDAO = jdbi.onDemand(FolderTreeNodeDAO.class);
        final UserDefinedPropertyDAO udpDAO = jdbi.onDemand(UserDefinedPropertyDAO.class);
        final DataTableColumnDAO dataTableColumnDAO = jdbi.onDemand(DataTableColumnDAO.class);
        final DataTableCellDAO dataTableCellDAO = jdbi.onDemand(DataTableCellDAO.class);
        final TestcaseRunDAO testcaseRunDAO = jdbi.onDemand(TestcaseRunDAO.class);
        final TestcaseIndividualRunDAO testcaseIndividualRunDAO = jdbi.onDemand(TestcaseIndividualRunDAO.class);
        final TeststepRunDAO teststepRunDAO = jdbi.onDemand(TeststepRunDAO.class);
        UserDAO userDAO = null;
        if (isInTeamMode(configuration)) {
            userDAO = jdbi.onDemand(UserDAO.class);
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
        //  order is important!!! (there are foreign keys linking them)
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
        udpDAO.createSequenceIfNotExists();
        udpDAO.createTableIfNotExists();
        dataTableColumnDAO.createSequenceIfNotExists();
        dataTableColumnDAO.createTableIfNotExists();
        dataTableCellDAO.createSequenceIfNotExists();
        dataTableCellDAO.createTableIfNotExists();
        testcaseRunDAO.createSequenceIfNotExists();
        testcaseRunDAO.createTableIfNotExists();
        testcaseIndividualRunDAO.createSequenceIfNotExists();
        testcaseIndividualRunDAO.createTableIfNotExists();
        teststepRunDAO.createSequenceIfNotExists();
        teststepRunDAO.createTableIfNotExists();
        if (isInTeamMode(configuration)) {
            userDAO.createSequenceIfNotExists();
            userDAO.createTableIfNotExists();
            userDAO.insertBuiltinAdminUserIfNotExists();
        }

        //  register APIs
        environment.jersey().register(new SystemResource(appInfo));
        environment.jersey().register(new ManagedEndpointResource(appInfo, endpointDAO));
        environment.jersey().register(new TestcaseResource(testcaseDAO, teststepDAO));
        environment.jersey().register(new FolderResource(folderDAO));
        environment.jersey().register(new FolderTreeNodeResource(folderTreeNodeDAO));
        environment.jersey().register(new TeststepResource(appInfo, teststepDAO, udpDAO, utilsDAO));
        environment.jersey().register(new WSDLResource());
        environment.jersey().register(new EnvironmentResource(environmentDAO));
        environment.jersey().register(new TestcaseRunResource(testcaseDAO, udpDAO, teststepDAO, utilsDAO,
                testcaseRunDAO, teststepRunDAO));
        environment.jersey().register(new AssertionResource(udpDAO, teststepDAO, utilsDAO));
        environment.jersey().register(new UDPResource(udpDAO));
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
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getSampleDatabase(), "sampleDatabase");

        //  create DAO objects
        final ArticleDAO articleDAO = jdbi.onDemand(ArticleDAO.class);

        //  create database tables
        articleDAO.createTableIfNotExists();

        //  register APIs
        environment.jersey().register(new ArticleResource(articleDAO));

        //  register SOAP web services
        jaxWsBundle.publishEndpoint(new EndpointBuilder("/article", new ArticleSOAP(articleDAO)));
    }
}
