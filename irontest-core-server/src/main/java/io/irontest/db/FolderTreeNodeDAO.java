package io.irontest.db;

import io.irontest.models.Folder;
import io.irontest.models.FolderTreeNode;
import io.irontest.models.FolderTreeNodeType;
import io.irontest.models.Testcase;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

@RegisterRowMapper(FolderTreeNodeMapper.class)
public interface FolderTreeNodeDAO extends CrossReferenceDAO {
    @SqlQuery("select id as id_per_type, name as text, parent_folder_id, 'folder' as type from folder " +
            "union " +
            "select id as id_per_type, name as text, parent_folder_id, 'testcase' as type from testcase")
    List<FolderTreeNode> findAll();

    @SqlUpdate("update testcase set name = :name, parent_folder_id = :parentFolderId, updated = CURRENT_TIMESTAMP where id = :id")
    void _updateTestcase(@Bind("name") String name, @Bind("parentFolderId") long parentFolderId,
                        @Bind("id") long id);

    @SqlUpdate("update folder set name = :name, parent_folder_id = :parentFolderId, updated = CURRENT_TIMESTAMP where id = :id")
    void _updateFolder(@Bind("name") String name, @Bind("parentFolderId") Long parentFolderId,
                       @Bind("id") long id);

    default void update(FolderTreeNode node) {
        if (FolderTreeNodeType.TESTCASE == node.getType()) {
            _updateTestcase(node.getText(), node.getParentFolderId(), node.getIdPerType());
        } else if (FolderTreeNodeType.FOLDER == node.getType()) {
            _updateFolder(node.getText(), node.getParentFolderId(), node.getIdPerType());
        }
    }

    @Transaction
    default FolderTreeNode insert(FolderTreeNode node) {
        if (node.getType() == FolderTreeNodeType.TESTCASE) {
            Testcase testcase = new Testcase();
            testcase.setParentFolderId(node.getParentFolderId());
            testcase = testcaseDAO().insert(testcase);
            dataTableDAO().createCaptionColumn(testcase.getId());
            node.setIdPerType(testcase.getId());
            node.setText(testcase.getName());
        } else if (node.getType() == FolderTreeNodeType.FOLDER) {
            Folder folder = folderDAO().insert(node.getParentFolderId());
            node.setIdPerType(folder.getId());
            node.setText(folder.getName());
        }
        return node;
    }
}
