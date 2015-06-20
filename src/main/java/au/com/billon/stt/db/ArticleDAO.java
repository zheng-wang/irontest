package au.com.billon.stt.db;

import au.com.billon.stt.models.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 20/06/2015.
 */
public class ArticleDAO {
    private List<Article> articles;

    public ArticleDAO() {
        articles = new ArrayList<Article>();
        articles.add(new Article(1, "Article 1 Title", "Article 1 Content"));
        articles.add(new Article(2, "Article 2 Title", "Article 2 Content"));
    }

    public List<Article> findAll() {
        return articles;
    }

    public Article findById(long articleId) {
        Article result = null;
        for (Article article: articles) {
            if (article.getId() == articleId) {
                result = article;
                break;
            }
        }
        return result;
    }
}
