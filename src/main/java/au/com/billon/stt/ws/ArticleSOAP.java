package au.com.billon.stt.ws;

import au.com.billon.stt.db.ArticleDAO;
import au.com.billon.stt.models.Article;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 30/06/2015.
 */
@WebService
public class ArticleSOAP {
    private final ArticleDAO dao;

    public ArticleSOAP(ArticleDAO dao) {
        this.dao = dao;
    }

    @WebMethod
    public List<Article> findAll() {
        return dao.findAll();
    }

    @WebMethod
    public List<Article> findByCreationTime(@WebParam(name = "startTime") Date startTime,
                                            @WebParam(name = "endTime") Date endTime) {
        return dao.findByCreationTime(startTime, endTime);
    }
}
