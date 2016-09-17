package io.irontest.db;

import io.irontest.models.Folder;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by Zheng on 10/09/2015.
 */
@RegisterMapper(FolderMapper.class)
public abstract class FolderDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS folder_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS folder (" +
            "id BIGINT DEFAULT folder_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, parent_folder_id BIGINT, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_folder_id) REFERENCES folder(id))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into folder (name) " +
               "select 'Root' where not exists (select 1 from folder where parent_folder_id is null)")
    public abstract void insertARootNodeIfNotExists();

    @SqlUpdate("insert into folder (parent_folder_id) values (:parentFolderId)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("parentFolderId") long parentFolderId);

    @SqlUpdate("update folder set name = :name where id = :id")
    protected abstract long updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @SqlQuery("select * from folder where id = :id")
    protected abstract Folder _findById(@Bind("id") long id);

    @Transaction
    public Folder insert(Long parentFolderId) {
        long id = _insert(parentFolderId);
        updateNameForInsert(id, "Folder " + id);
        return _findById(id);
    }
}
