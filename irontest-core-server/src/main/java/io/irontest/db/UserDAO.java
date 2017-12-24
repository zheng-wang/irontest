package io.irontest.db;

import io.irontest.core.HashedPassword;
import io.irontest.utils.PasswordUtils;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import static io.irontest.IronTestConstants.*;

/**
 * Created by Zheng on 23/12/2017.
 */
public abstract class UserDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    public abstract void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS user (" +
            "id BIGINT DEFAULT user_sequence.NEXTVAL PRIMARY KEY, username VARCHAR(100) NOT NULL, " +
            "password VARCHAR(100) NOT NULL, salt VARCHAR(100) NOT NULL, " +
            "password_hashing_alg VARCHAR(100) NOT NULL, kdf_iterations INT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONSTRAINT USER_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(username)," +
            "CONSTRAINT USER_" + DB_USERNAME_CONSTRAINT_NAME_SUFFIX +
                " CHECK(LENGTH(username) >= 3 AND REGEXP_LIKE(username, '^\\w+$')))")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into user (username, password, salt, password_hashing_alg, kdf_iterations) " +
            "select '" + SYSADMIN_USER + "', :password, :salt, '" + PASSWORD_HASHING_ALGORITHM + "', " + KDF_ITERATIONS + " " +
            "where not exists (select 1 from user where username = '" + SYSADMIN_USER + "')")
    protected abstract void _insertBuiltinAdminUserIfNotExists(@Bind("password") String password,
                                                            @Bind("salt") String salt);

    public void insertBuiltinAdminUserIfNotExists() {
        HashedPassword hashedPassword = PasswordUtils.hashPassword(SYSADMIN_USER_DEFAULT_PASSWORD);
        _insertBuiltinAdminUserIfNotExists(hashedPassword.getHashedPassword(), hashedPassword.getSalt());
    }
}
