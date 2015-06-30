package au.com.billon.stt;

import au.com.billon.stt.db.ArticleDAO;
import au.com.billon.stt.db.EndpointDAO;
import au.com.billon.stt.resources.ArticleResource;
import au.com.billon.stt.resources.EndpointResource;
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

    @Override
    public String getName() {
        return "service-testing-tool";
    }

    @Override
    public void initialize(Bootstrap<STTConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/app", "/ui"));
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(STTConfiguration configuration, Environment environment) throws Exception {
        //  create database tables
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabase(), "h2");
        final ArticleDAO articleDAO = jdbi.onDemand(ArticleDAO.class);
        final EndpointDAO endpointDAO = jdbi.onDemand(EndpointDAO.class);
        articleDAO.createTableIfNotExists();
        endpointDAO.createTableIfNotExists();

        //  register REST resources
        environment.jersey().register(new ArticleResource(articleDAO));
        environment.jersey().register(new EndpointResource(endpointDAO));
    }

}
