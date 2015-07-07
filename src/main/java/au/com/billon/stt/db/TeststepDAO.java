package au.com.billon.stt.db;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by Zheng on 7/07/2015.
 */
public interface TeststepDAO {
    @SqlUpdate("create table IF NOT EXISTS teststep (id INT PRIMARY KEY auto_increment, testcase_id INT, name varchar(200), description clob, created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP, type varchar(20), request clob)")
    void createTableIfNotExists();
}
