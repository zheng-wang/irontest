package io.irontest.db;

import io.irontest.models.FolderTreeNode;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 10/09/2015.
 */
@RegisterMapper(FolderTreeNodeMapper.class)
public abstract class FolderTreeNodeDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS foldertree_node_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS foldertree_node (" +
            "id BIGINT DEFAULT foldertree_node_sequence.NEXTVAL PRIMARY KEY, parent_id BIGINT, " +
            "text varchar(200), type varchar(50) NOT NULL, testcase_id BIGINT, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_id) REFERENCES foldertree_node(id), " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into foldertree_node (text, type) " +
               "select 'Root', 'folder' where not exists (select 1 from foldertree_node where parent_id is null)")
    public abstract void insertARootNodeIfNotExists();

    @SqlQuery("select n.id, n.parent_id, n.type, n.testcase_id, " +
            "CASE WHEN n.testcase_id is null THEN n.text ELSE t.name END as text " +
            "from foldertree_node n left outer join testcase t on t.id = n.testcase_id")
    public abstract List<FolderTreeNode> findAll();
}
