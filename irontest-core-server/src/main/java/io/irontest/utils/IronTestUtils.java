package io.irontest.utils;

import io.irontest.core.runner.SQLStatementType;
import io.irontest.models.DataTable;
import io.irontest.models.DataTableColumn;
import io.irontest.models.UserDefinedProperty;
import org.jdbi.v3.core.statement.Script;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public final class IronTestUtils {
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

    public static boolean isSQLRequestSingleSelectStatement(String sqlRequest) {
        List<String> statements = getStatements(sqlRequest);
        return statements.size() == 1 && SQLStatementType.isSelectStatement(statements.get(0));
    }

    public static List<String> getStatements(String sqlRequest) {
        List<String> statements = null;
//        if ("".equals(sqlRequest)) {      //  if passing "" to handle.createScript(), script.getStatements() returns unexpected values
//            statements = new ArrayList<String>();
//        } else {                          //  parse the SQL script
            Script script = new Script(null, sqlRequest);
            statements = script.getStatements();
//        }
        return statements;
    }

    public static Map<String, String> udpListToMap(List<UserDefinedProperty> testcaseUDPs) {
        Map<String, String> result = new HashMap<>();
        for (UserDefinedProperty udp: testcaseUDPs) {
            result.put(udp.getName(), udp.getValue());
        }
        return result;
    }

    public static void checkDuplicatePropertyNameBetweenDataTableAndUPDs(Set<String> udpNames, DataTable dataTable) {
        Set<String> set = new HashSet<>();
        set.addAll(udpNames);
        for (DataTableColumn dataTableColumn : dataTable.getColumns()) {
            if (!set.add(dataTableColumn.getName())) {
                throw new RuntimeException("Duplicate property name between data table and UDPs: " + dataTableColumn.getName());
            }
        }
    }
}
