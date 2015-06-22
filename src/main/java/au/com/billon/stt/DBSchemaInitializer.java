package au.com.billon.stt;

import io.dropwizard.db.DataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Zheng on 22/06/2015.
 */
public class DBSchemaInitializer {
    private String[] statements = new String[] {
        "create table IF NOT EXISTS article (id INT PRIMARY KEY, title varchar(50), content varchar(500))",
        "create table IF NOT EXISTS article2 (id INT PRIMARY KEY, title varchar(50), content varchar(500))"
    };

    public void init(DataSourceFactory dbConfig) throws Exception {
        Class.forName(dbConfig.getDriverClass());
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());
            stmt = conn.createStatement();
            for (String statement: statements) {
                stmt.execute(statement);
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
