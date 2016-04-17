package io.irontest.db;

import io.irontest.models.Endpoint;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng Wang on 4/18/16.
 */
public interface UtilsDAO {
    @SqlQuery("select TRIM(CHAR(0) FROM UTF8TOSTRING(DECRYPT('AES', '8888', :encryptedPassword)))")
    String decryptPassword(@Bind("encryptedPassword") String encryptedPassword);
}
