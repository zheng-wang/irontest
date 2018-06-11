package io.irontest.db;

import io.irontest.models.Folder;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderMapper implements ResultSetMapper<Folder> {
    public Folder map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Folder folder = new Folder(rs.getLong("id"), rs.getString("name"),
                rs.getString("description"));
        return folder;
    }
}
