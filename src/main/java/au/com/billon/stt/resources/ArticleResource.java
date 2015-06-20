package au.com.billon.stt.resources;

import au.com.billon.stt.db.ArticleDAO;
import au.com.billon.stt.models.Article;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 20/06/2015.
 */
@Path("/articles") @Produces({ MediaType.APPLICATION_JSON })
public class ArticleResource {
    private final ArticleDAO articleDAO = new ArticleDAO();

    @GET
    public List<Article> findAll() {
        List<Article> result = articleDAO.findAll();
        System.out.println(result);
        return result;
    }

    @GET @Path("{articleId}")
    public Article findById(@PathParam("articleId") long articleId) {
        return articleDAO.findById(articleId);
    }
}
