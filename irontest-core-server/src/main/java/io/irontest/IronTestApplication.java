package io.irontest;

import com.roskart.dropwizard.jaxws.EndpointBuilder;
import com.roskart.dropwizard.jaxws.JAXWSBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.*;
import io.irontest.resources.*;
import io.irontest.ws.ArticleSOAP;
import org.glassfish.jersey.filter.LoggingFilter;
import org.skife.jdbi.v2.DBI;

import java.util.logging.Logger;

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
        return "Iron Test";
    }

    @Override
    public void initialize(Bootstrap<IronTestConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/ui"));
        bootstrap.addBundle(jaxWsBundle);
        bootstrap.addBundle(new MultiPartBundle());
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
        final EnvironmentDAO environmentDAO = jdbi.onDemand(EnvironmentDAO.class);
        final UtilsDAO utilsDAO = jdbi.onDemand(UtilsDAO.class);

        //  create database tables
        //  order is important!!! (there are foreign keys linking them)
        articleDAO.createTableIfNotExists();
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

        //  register REST resources
        environment.jersey().register(new ArticleResource(articleDAO));
        environment.jersey().register(new EndpointResource(endpointDAO));
        environment.jersey().register(new TestcaseResource(testcaseDAO, teststepDAO));
        environment.jersey().register(new TeststepResource(teststepDAO));
        environment.jersey().register(new WSDLResource());
        environment.jersey().register(new EnvironmentResource(environmentDAO));
        environment.jersey().register(new TestrunResource(teststepDAO, utilsDAO));

        //  register JSON services
        environment.jersey().register(new JSONService(new AssertionVerifierFactory(), endpointDAO));

        //  register SOAP web services
        jaxWsBundle.publishEndpoint(new EndpointBuilder("/article", new ArticleSOAP(articleDAO)));

        //  register jersey LoggingFilter
        environment.jersey().register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true));

        //  register exception mappers
        environment.jersey().register(new IronTestLoggingExceptionMapper());
    }
}
