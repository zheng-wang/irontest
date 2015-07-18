package au.com.billon.stt;

import au.com.billon.stt.db.*;
import au.com.billon.stt.exceptions.STTDBIExceptionMapper;
import au.com.billon.stt.resources.*;
import au.com.billon.stt.ws.ArticleSOAP;
import com.roskart.dropwizard.jaxws.EndpointBuilder;
import com.roskart.dropwizard.jaxws.JAXWSBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

/**
 * Created by Zheng on 20/06/2015.
 */
public class STTApplication extends Application<STTConfiguration> {
    public static void main(String[] args) throws Exception {
        new STTApplication().run(args);
    }

    private JAXWSBundle jaxWsBundle = new JAXWSBundle();

    @Override
    public String getName() {
        return "service-testing-tool";
    }

    @Override
    public void initialize(Bootstrap<STTConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/ui"));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(jaxWsBundle);
    }

    @Override
    public void run(STTConfiguration configuration, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabase(), "h2");

        //  create DAO objects
        final ArticleDAO articleDAO = jdbi.onDemand(ArticleDAO.class);
        final EndpointDAO endpointDAO = jdbi.onDemand(EndpointDAO.class);
        final TestcaseDAO testcaseDAO = jdbi.onDemand(TestcaseDAO.class);
        final TeststepDAO teststepDAO = jdbi.onDemand(TeststepDAO.class);
        final TeststepPropertyDAO teststepPropertyDAO = jdbi.onDemand(TeststepPropertyDAO.class);
        final IntfaceDAO intfaceDAO = jdbi.onDemand(IntfaceDAO.class);
        final EnvironmentDAO environmentDAO = jdbi.onDemand(EnvironmentDAO.class);
        final EnvEntryDAO enventryDAO = jdbi.onDemand(EnvEntryDAO.class);
        final EndpointDetailDAO endpointdtlDAO = jdbi.onDemand(EndpointDetailDAO.class);

        //  create database tables        
        articleDAO.createTableIfNotExists();
        endpointDAO.createTableIfNotExists();
        testcaseDAO.createTableIfNotExists();
        teststepDAO.createTableIfNotExists();
        teststepPropertyDAO.createTableIfNotExists();
        //intfaceDAO.createTableIfNotExists();
        environmentDAO.createTableIfNotExists();
        enventryDAO.createTableIfNotExists();
        endpointdtlDAO.createTableIfNotExists();

        //  register REST resources
        environment.jersey().register(new ArticleResource(articleDAO));
        environment.jersey().register(new EndpointResource(endpointDAO, endpointdtlDAO));
        environment.jersey().register(new TestcaseResource(testcaseDAO, teststepDAO));
        environment.jersey().register(new TeststepResource(teststepDAO, teststepPropertyDAO));
        environment.jersey().register(new WSDLResource());
        environment.jersey().register(new IntfaceResource(intfaceDAO));
        environment.jersey().register(new EnvironmentResource(environmentDAO));
        environment.jersey().register(new EnvEntryResource(enventryDAO));

        //  register exception mappers
        environment.jersey().register(new STTDBIExceptionMapper());

        //  register SOAP web services
        jaxWsBundle.publishEndpoint(new EndpointBuilder("/article", new ArticleSOAP(articleDAO)));
    }
}
