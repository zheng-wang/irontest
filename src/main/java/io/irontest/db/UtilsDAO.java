package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import static io.irontest.IronTestConstants.PASSWORD_ENCRYPTION_KEY;

/**
 * Created by Zheng Wang on 4/18/16.
 */
public interface UtilsDAO {
    @SqlQuery("select TRIM(CHAR(0) FROM UTF8TOSTRING(DECRYPT('AES', '" + PASSWORD_ENCRYPTION_KEY + "', :encryptedPassword)))")
    String decryptPassword(@Bind("encryptedPassword") String encryptedPassword);
}
