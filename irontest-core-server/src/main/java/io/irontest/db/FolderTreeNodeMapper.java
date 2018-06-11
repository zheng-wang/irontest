package io.irontest.db;

import io.irontest.models.FolderTreeNode;
import io.irontest.models.FolderTreeNodeType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderTreeNodeMapper implements ResultSetMapper<FolderTreeNode> {
    public FolderTreeNode map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        FolderTreeNode folderTreeNode = new FolderTreeNode(rs.getLong("id_per_type"),
                rs.getObject("parent_folder_id") == null ? null : rs.getLong("parent_folder_id"),
                rs.getString("text"), FolderTreeNodeType.getByText(rs.getString("type")));

        return folderTreeNode;
    }
}
