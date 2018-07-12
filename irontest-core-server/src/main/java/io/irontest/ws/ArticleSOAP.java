package io.irontest.ws;

import io.irontest.db.ArticleDAO;
import io.irontest.models.Article;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;

@WebService
public class ArticleSOAP {
    private final ArticleDAO dao;

    public ArticleSOAP(ArticleDAO dao) {
        this.dao = dao;
    }

    @WebMethod
    public List<Article> findAllArticles() {
        return dao.findAll();
    }

    @WebMethod
    public List<Article> findArticlesByCreationTimeRange(@WebParam(name = "startTime") Date startTime,
                                            @WebParam(name = "endTime") Date endTime) {
        return dao.findByCreationTimeRange(startTime, endTime);
    }

    @WebMethod
    public Article createArticle(@WebParam(name = "title") String title, @WebParam(name = "content") String content) {
        Article article = new Article(0, title, content);
        long id = dao.insert(article);
        return dao.findById(id);
    }

    @WebMethod
    public int updateArticleByTitle(@WebParam(name = "title") String title, @WebParam(name = "content") String content) {
        return dao.updateByTitle(title, content);
    }

    @WebMethod
    public int deleteArticleById(@WebParam(name = "id") long id) {
        return dao.deleteById(id);
    }
}
