package io.irontest.db;

import io.irontest.models.Folder;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderMapper implements RowMapper<Folder> {
    public Folder map(ResultSet rs, StatementContext ctx) throws SQLException {
        Folder folder = new Folder(rs.getLong("id"), rs.getString("name"),
                rs.getString("description"));
        return folder;
    }
}
