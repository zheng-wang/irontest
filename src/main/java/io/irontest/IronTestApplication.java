package io.irontest;

import com.roskart.dropwizard.jaxws.EndpointBuilder;
import com.roskart.dropwizard.jaxws.JAXWSBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.core.assertion.EvaluatorFactory;
import io.irontest.db.*;
import io.irontest.exceptions.IronTestDBIExceptionMapper;
import io.irontest.resources.*;
import io.irontest.ws.ArticleSOAP;
import org.skife.jdbi.v2.DBI;

/**
 * Created by Zheng on 20/06/2015.
 */
public class IronTestApplication extends Application<IronTestConfiguration> {
    public static void main(String[] args) throws Exception {
        new IronTestApplication().run(args);
    }

    private JAXWSBundle jaxWsBundle = new JAXWSBundle();

    @Override
    public String getName() {
        return "iron-test";
    }

    @Override
    public void initialize(Bootstrap<IronTestConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/ui"));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(jaxWsBundle);
    }

    @Override
    public void run(IronTestConfiguration configuration, Environment environment) throws Exception {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabase(), "h2");

        //  create DAO objects
        final ArticleDAO articleDAO = jdbi.onDemand(ArticleDAO.class);
        final EndpointDAO endpointDAO = jdbi.onDemand(EndpointDAO.class);
        final TestcaseDAO testcaseDAO = jdbi.onDemand(TestcaseDAO.class);
        final TeststepDAO teststepDAO = jdbi.onDemand(TeststepDAO.class);
        final AssertionDAO assertionDAO = jdbi.onDemand(AssertionDAO.class);
        final IntfaceDAO intfaceDAO = jdbi.onDemand(IntfaceDAO.class);
        final EnvironmentDAO environmentDAO = jdbi.onDemand(EnvironmentDAO.class);
        final EnvEntryDAO enventryDAO = jdbi.onDemand(EnvEntryDAO.class);
        final EndpointDetailDAO endpointdtlDAO = jdbi.onDemand(EndpointDetailDAO.class);

        //  create database tables        
        articleDAO.createTableIfNotExists();
        endpointDAO.createTableIfNotExists();
        endpointdtlDAO.createTableIfNotExists();
        intfaceDAO.createTableIfNotExists();
        intfaceDAO.initSystemData();
        environmentDAO.createTableIfNotExists();
        enventryDAO.createTableIfNotExists();
        testcaseDAO.createTableIfNotExists();
        teststepDAO.createTableIfNotExists();
        assertionDAO.createTableIfNotExists();

        final EvaluatorFactory evaluatorFactory = new EvaluatorFactory();

        //  register REST resources
        environment.jersey().register(new ArticleResource(articleDAO));
        environment.jersey().register(new EndpointResource(endpointDAO, endpointdtlDAO));
        environment.jersey().register(new TestcaseResource(testcaseDAO, teststepDAO));
        environment.jersey().register(new TeststepResource(teststepDAO));
        environment.jersey().register(new AssertionResource(assertionDAO));
        environment.jersey().register(new WSDLResource());
        environment.jersey().register(new IntfaceResource(intfaceDAO));
        environment.jersey().register(new EnvironmentResource(environmentDAO, enventryDAO));
        environment.jersey().register(new TestrunResource(endpointDAO, endpointdtlDAO, testcaseDAO, teststepDAO,
                assertionDAO));

        //  register JSON services
        environment.jersey().register(new JSONService(new AssertionVerifierFactory()));

        //  register exception mappers
        environment.jersey().register(new IronTestDBIExceptionMapper());

        //  register SOAP web services
        jaxWsBundle.publishEndpoint(new EndpointBuilder("/article", new ArticleSOAP(articleDAO)));
    }
}
