package io.irontest.db;

import io.irontest.core.HashedPassword;
import io.irontest.models.User;
import io.irontest.utils.PasswordUtils;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.irontest.IronTestConstants.*;

@RegisterRowMapper(UserMapper.class)
public interface UserDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS user (" +
            "id BIGINT DEFAULT user_sequence.NEXTVAL PRIMARY KEY, username VARCHAR(100) NOT NULL, " +
            "password VARCHAR(100) NOT NULL, salt VARCHAR(100) NOT NULL, roles VARCHAR(500), " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT USER_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(username)," +
            "CONSTRAINT USER_" + DB_USERNAME_CONSTRAINT_NAME_SUFFIX +
                " CHECK(LENGTH(username) >= 3 AND REGEXP_LIKE(username, '^\\w+$')))")
    void createTableIfNotExists();

    @SqlUpdate("insert into user (username, password, salt, roles) " +
            "select '" + SYSADMIN_USER + "', :password, :salt, '[\"" + USER_ROLE_ADMIN + "\"]' " +
            "where not exists (select 1 from user where username = '" + SYSADMIN_USER + "')")
    void _insertBuiltinAdminUserIfNotExists(@Bind("password") String password,
                                            @Bind("salt") String salt);

    default void insertBuiltinAdminUserIfNotExists() {
        HashedPassword hashedPassword = PasswordUtils.hashPassword(USER_DEFAULT_PASSWORD);
        _insertBuiltinAdminUserIfNotExists(hashedPassword.getHashedPassword(), hashedPassword.getSalt());
    }

    @SqlQuery("select id, username, password, salt, roles from user where username = :username")
    User findByUsername(@Bind("username") String username);

    @SqlQuery("select id, username from user")
    List<User> findAll();

    @SqlQuery("select id, username, salt from user where id = :id")
    User findById(@Bind("id") long id);

    @SqlUpdate("insert into user (username, password, salt) values (:username, :password, :salt)")
    @GetGeneratedKeys
    long _insert(@Bind("username") String username, @Bind("password") String password,
                 @Bind("salt") String salt);

    @Transaction
    default User insert(String username) {
        HashedPassword hashedPassword = PasswordUtils.hashPassword(USER_DEFAULT_PASSWORD);
        long id = _insert(username, hashedPassword.getHashedPassword(), hashedPassword.getSalt());
        return findById(id);
    }

    @SqlUpdate("delete from user where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlUpdate("update user set password = :password, salt = :salt, updated = CURRENT_TIMESTAMP where id = :id")
    void _updatePassword(@Bind("id") long id, @Bind("password") String password,
                         @Bind("salt") String salt);

    default void updatePassword(long userId, String newPassword) {
        HashedPassword hashedNewPassword = PasswordUtils.hashPassword(newPassword);
        _updatePassword(userId, hashedNewPassword.getHashedPassword(), hashedNewPassword.getSalt());
    }
}