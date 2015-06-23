package au.com.billon.stt.resources;

import au.com.billon.stt.db.ArticleDAO;
import au.com.billon.stt.models.Article;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Zheng on 20/06/2015.
 */
@Path("/articles") @Produces({ MediaType.APPLICATION_JSON })
public class ArticleResource {
    private final ArticleDAO dao;

    public ArticleResource(ArticleDAO dao) {
        this.dao = dao;
    }

    @POST
    public Article create(Article article) {
        long id = dao.insert(article);
        article.setId(id);
        return article;
    }

    @PUT @Path("{articleId}")
    public Article update(Article article) {
        dao.update(article);
        return article;
    }

    @DELETE @Path("{articleId}")
    public void delete(@PathParam("articleId") long articleId) {
        dao.deleteById(articleId);
    }

    @GET
    public List<Article> findAll() {
        return dao.findAll();
    }

    @GET @Path("{articleId}")
    public Article findById(@PathParam("articleId") long articleId) {
        return dao.findById(articleId);
    }
}
