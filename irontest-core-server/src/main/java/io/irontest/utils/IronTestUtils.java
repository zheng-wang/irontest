package io.irontest.utils;

import io.irontest.core.runner.SQLStatementType;
import io.irontest.models.UserDefinedProperty;
import org.skife.jdbi.v2.Script;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IronTestUtils {
    /**
     * @param rs
     * @return a list of lower case column names present in the result set.
     * @throws SQLException
     */
    public static List<String> getFieldsPresentInResultSet(ResultSet rs) throws SQLException {
        List<String> fieldsPresentInResultSet = new ArrayList<String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for(int index =1; index <= metaData.getColumnCount(); index++) {
            fieldsPresentInResultSet.add(metaData.getColumnLabel(index).toLowerCase());
        }
        return fieldsPresentInResultSet;
    }

    public static boolean isSQLRequestSingleSelectStatement(String sqlRequest) throws Exception {
        List<String> statements = getStatements(sqlRequest);
        return statements.size() == 1 && SQLStatementType.isSelectStatement(statements.get(0));
    }

    public static List<String> getStatements(String sqlRequest) throws Exception {
        List<String> statements = null;
        if ("".equals(sqlRequest)) {      //  if passing "" to handle.createScript(), script.getStatements() returns unexpected values
            statements = new ArrayList<String>();
        } else {                          //  parse the SQL script
            Constructor constructor = Script.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            Script script = (Script) constructor.newInstance(null, null, null, null);
            Method method = Script.class.getDeclaredMethod("splitToStatements", String.class);
            method.setAccessible(true);
            statements = (List<String>) method.invoke(script, sqlRequest);
        }
        return statements;
    }

    public static Map<String, String> udpListToMap(List<UserDefinedProperty> testcaseUDPs) {
        Map<String, String> result = new HashMap<>();
        for (UserDefinedProperty udp: testcaseUDPs) {
            result.put(udp.getName(), udp.getValue());
        }
        return result;
    }
}
