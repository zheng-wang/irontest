package io.irontest.db;

import io.irontest.models.Folder;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import static io.irontest.IronTestConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(FolderMapper.class)
public interface FolderDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS folder_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS folder (" +
            "id BIGINT DEFAULT folder_sequence.NEXTVAL PRIMARY KEY, " +
            "name varchar(200) NOT NULL DEFAULT CURRENT_TIMESTAMP, description CLOB, parent_folder_id BIGINT, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_folder_id) REFERENCES folder(id), " +
            "CONSTRAINT FOLDER_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(parent_folder_id, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into folder (name) " +
               "select 'Root' where not exists (select 1 from folder where parent_folder_id is null)")
    void insertARootNodeIfNotExists();

    @SqlUpdate("insert into folder (parent_folder_id) values (:parentFolderId)")
    @GetGeneratedKeys
    long _insert(@Bind("parentFolderId") long parentFolderId);

    @SqlUpdate("update folder set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @SqlQuery("select * from folder where id = :id")
    Folder _findById(@Bind("id") long id);

    @Transaction
    default Folder insert(Long parentFolderId) {
        long id = _insert(parentFolderId);
        updateNameForInsert(id, "Folder " + id);
        return _findById(id);
    }

    @SqlUpdate("update folder set name = :name, description = :description, " +
            "updated = CURRENT_TIMESTAMP where id = :id")
    void update(@BindBean Folder folder);
}
