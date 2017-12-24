package io.irontest.db;

import io.irontest.core.HashedPassword;
import io.irontest.models.User;
import io.irontest.utils.PasswordUtils;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 23/12/2017.
 */
@RegisterMapper(UserMapper.class)
public abstract class UserDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS user (" +
            "id BIGINT DEFAULT user_sequence.NEXTVAL PRIMARY KEY, username VARCHAR(100) NOT NULL, " +
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
        HashedPassword hashedPassword = PasswordUtils.hashPassword(SYSADMIN_USER_DEFAULT_PASSWORD);
        _insertBuiltinAdminUserIfNotExists(hashedPassword.getHashedPassword(), hashedPassword.getSalt());
    }

    @SqlQuery("select username, password, salt from user where username = :username")
    public abstract User findByUsername(@Bind("username") String username);
}