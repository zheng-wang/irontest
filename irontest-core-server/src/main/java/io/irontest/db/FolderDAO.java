package io.irontest.db;

import io.irontest.models.FolderTreeNode;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

/**
 * Created by Zheng on 10/09/2015.
 */
public abstract class FolderDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS folder_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS folder (" +
            "id BIGINT DEFAULT folder_sequence.NEXTVAL PRIMARY KEY, name varchar(200) NOT NULL, " +
            "parent_folder_id BIGINT, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_folder_id) REFERENCES folder(id))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into folder (name) " +
               "select 'Root' where not exists (select 1 from folder where parent_folder_id is null)")
    public abstract void insertARootNodeIfNotExists();
}
