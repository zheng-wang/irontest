package au.com.billon.stt;

import au.com.billon.stt.resources.ArticleResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
    }

    @Override
    public void run(STTConfiguration configuration, Environment environment) throws Exception {
        //  create database tables
        new DBSchemaInitializer().init(configuration.getDatabase());

        //  register REST resources
        final ArticleResource resource = new ArticleResource();
        environment.jersey().register(resource);
    }

}
