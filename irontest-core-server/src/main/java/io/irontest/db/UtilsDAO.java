package io.irontest.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static io.irontest.IronTestConstants.ENDPOINT_PASSWORD_ENCRYPTION_KEY;

/**
 * Created by Zheng Wang on 4/18/16.
 */
public abstract class UtilsDAO {
    @SqlQuery("select TRIM(CHAR(0) FROM UTF8TOSTRING(DECRYPT('AES', '" + ENDPOINT_PASSWORD_ENCRYPTION_KEY + "', :encryptedPassword)))")
    public abstract String decryptEndpointPassword(@Bind("encryptedPassword") String encryptedPassword);

    public List<LinkedHashMap<String, Object>> getTestcaseDataTable(long testcaseId) {
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        return result;
    }
}
