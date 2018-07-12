package io.irontest.db;

import io.irontest.models.Article;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleMapper implements RowMapper<Article> {
    public Article map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Article(rs.getLong("id"), rs.getString("title"), rs.getString("content"));
    }
}
