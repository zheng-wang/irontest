package io.irontest.db;

import io.irontest.core.HashedPassword;
import io.irontest.models.User;
import io.irontest.utils.PasswordUtils;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 23/12/2017.
 */
@RegisterMapper(UserMapper.class)
public abstract class UserDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS user (" +
            "id BIGINT DEFAULT user_sequence.NEXTVAL PRIMARY KEY, " +
            "username VARCHAR(100) NOT NULL DEFAULT DATEDIFF('ms', '1970-01-01', CURRENT_TIMESTAMP), " +
            "password VARCHAR(100) NOT NULL, salt VARCHAR(100) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT USER_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(username)," +
            "CONSTRAINT USER_" + DB_USERNAME_CONSTRAINT_NAME_SUFFIX +
                " CHECK(LENGTH(username) >= 3 AND REGEXP_LIKE(username, '^\\w+$')))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into user (username, password, salt) " +
            "select '" + SYSADMIN_USER + "', :password, :salt " +
            "where not exists (select 1 from user where username = '" + SYSADMIN_USER + "')")
    protected abstract void _insertBuiltinAdminUserIfNotExists(@Bind("password") String password,
                                                            @Bind("salt") String salt);

    public void insertBuiltinAdminUserIfNotExists() {
        HashedPassword hashedPassword = PasswordUtils.hashPassword(USER_DEFAULT_PASSWORD);
        _insertBuiltinAdminUserIfNotExists(hashedPassword.getHashedPassword(), hashedPassword.getSalt());
    }

    @SqlQuery("select id, username, password, salt from user where username = :username")
    public abstract User findByUsername(@Bind("username") String username);

    @SqlQuery("select id, username from user")
    public abstract List<User> findAll();

    @SqlQuery("select id, username from user where id = :id")
    public abstract User findById(@Bind("id") long id);

    @SqlUpdate("insert into user (password, salt) values (:password, :salt)")
    @GetGeneratedKeys
    protected abstract long _insert(@Bind("password") String password,
                                    @Bind("salt") String salt);

    @SqlUpdate("update user set username = :username where id = :id")
    protected abstract long updateUsernameForInsert(@Bind("id") long id, @Bind("username") String username);

    @Transaction
    public User insert() {
        HashedPassword hashedPassword = PasswordUtils.hashPassword(USER_DEFAULT_PASSWORD);
        long id = _insert(hashedPassword.getHashedPassword(), hashedPassword.getSalt());
        updateUsernameForInsert(id, "user" + id);
        return findById(id);
    }
}