package io.irontest.db;

import io.irontest.models.Folder;
import io.irontest.models.FolderTreeNode;
import io.irontest.models.FolderTreeNodeType;
import io.irontest.models.Testcase;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 10/09/2015.
 */
@RegisterMapper(FolderTreeNodeMapper.class)
public abstract class FolderTreeNodeDAO {
    @CreateSqlObject
    protected abstract TestcaseDAO testcaseDAO();

    @CreateSqlObject
    protected abstract FolderDAO folderDAO();

    @SqlQuery("select id as id_per_type, name as text, parent_folder_id, 'folder' as type from folder " +
            "union " +
            "select id as id_per_type, name as text, parent_folder_id, 'testcase' as type from testcase")
    public abstract List<FolderTreeNode> findAll();

    @SqlUpdate("update testcase set name = :name, parent_folder_id = :parentFolderId, updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _updateTestcase(@Bind("name") String name, @Bind("parentFolderId") long parentFolderId,
                                           @Bind("id") long id);

    @SqlUpdate("update folder set name = :name, parent_folder_id = :parentFolderId, updated = CURRENT_TIMESTAMP where id = :id")
    protected abstract int _updateFolder(@Bind("name") String name, @Bind("parentFolderId") long parentFolderId,
                                           @Bind("id") long id);

    public void update(FolderTreeNode node) {
        if (FolderTreeNodeType.TESTCASE == node.getType()) {
            _updateTestcase(node.getText(), node.getParentFolderId(), node.getIdPerType());
        } else if (FolderTreeNodeType.FOLDER == node.getType()) {
            _updateFolder(node.getText(), node.getParentFolderId(), node.getIdPerType());
        }
    }

    public FolderTreeNode insert(FolderTreeNode node) {
        if (node.getType() == FolderTreeNodeType.TESTCASE) {
            Testcase testcase = new Testcase();
            testcase.setParentFolderId(node.getParentFolderId());
            testcase = testcaseDAO().insert(testcase);
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
