package io.irontest.db;

import io.irontest.models.FolderTreeNode;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 10/09/2015.
 */
@RegisterMapper(FolderTreeNodeMapper.class)
public abstract class FolderTreeNodeDAO {
    @SqlQuery("select id as id_per_type, name as text, parent_folder_id, 'folder' as type from folder " +
            "union " +
            "select id as id_per_type, name as text, parent_folder_id, 'testcase' as type from testcase")
    public abstract List<FolderTreeNode> findAll();
}
