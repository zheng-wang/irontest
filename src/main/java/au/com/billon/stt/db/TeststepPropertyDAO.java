package au.com.billon.stt.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Zheng on 7/07/2015.
 */
public interface TeststepPropertyDAO {
    @SqlUpdate("create table IF NOT EXISTS teststep_property (id INT PRIMARY KEY auto_increment, teststep_id INT, name varchar(100), value clob, created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();
}
