package io.irontest.db;

import io.irontest.models.Article;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zheng on 23/06/2015.
 */
public class ArticleMapper implements ResultSetMapper<Article> {
    public Article map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Article(rs.getLong("id"), rs.getString("title"), rs.getString("content"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));
    }
}
