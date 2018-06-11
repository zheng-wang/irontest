package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import static io.irontest.IronTestConstants.ENDPOINT_PASSWORD_ENCRYPTION_KEY;

public abstract class UtilsDAO {
    @SqlQuery("select TRIM(CHAR(0) FROM UTF8TOSTRING(DECRYPT('AES', '" + ENDPOINT_PASSWORD_ENCRYPTION_KEY + "', :encryptedPassword)))")
    public abstract String decryptEndpointPassword(@Bind("encryptedPassword") String encryptedPassword);
}
