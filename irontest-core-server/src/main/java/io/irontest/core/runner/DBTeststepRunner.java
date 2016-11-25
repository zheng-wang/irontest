package io.irontest.core.runner;

import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Script;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class DBTeststepRunner extends TeststepRunner {
    protected DBAPIResponse run(Teststep teststep) throws Exception {
        DBAPIResponse response = new DBAPIResponse();
        String request = (String) teststep.getRequest();
        Endpoint endpoint = teststep.getEndpoint();
        DBI jdbi = new DBI(endpoint.getUrl(), endpoint.getUsername(), endpoint.getPassword());
        Handle handle = jdbi.open();

        //  get SQL statements (trimmed and without comments) and JDBI script object
        List<String> statements = null;
        Script script = null;
        if ("".equals(request)) {      //  if passing "" to handle.createScript(), script.getStatements() returns unexpected values
            statements = new ArrayList<String>();
        } else {
            //  parse the SQL script (code could be broken since JDK 9 when setAccessible will be gone)
            script = handle.createScript(request);
            Method method = script.getClass().getDeclaredMethod("getStatements");
            method.setAccessible(true);
            statements = (List<String>) method.invoke(script);
        }
        System.out.println(statements);
        sanityCheckTheStatements(statements);

        if (statements.get(0).startsWith("select ")) {    //  the request is a select statement
            Query<Map<String, Object>> query = handle.createQuery(request);
            List<Map<String, Object>> resultSet = query.list();
            response.setResultSet(resultSet);
            response.setNumberOfRowsModified(-1);
        } else {                                          //  the request is one or more non-select statements
            int[] returnValues = script.execute();
            for (int number: returnValues) {
                System.out.println(number);
            }
            response.setNumberOfRowsModified(returnValues[0]);
        }

        handle.close();

        return response;
    }

    /**
     * Throw exception if the statements are not supported.
     * @param statements
     * @throws Exception
     */
    private void sanityCheckTheStatements(List<String> statements) throws Exception {
        if (statements.size() == 0) {
            throw new Exception("No SQL statement to run.");
        }

        int selectStatementCount = 0;
        boolean nonSelectStatementExists = false;
        for (String statement: statements) {
            statement = statement.toLowerCase();
            if (!(statement.startsWith("select ") || statement.startsWith("insert ") ||
                    statement.startsWith("update ") || statement.startsWith("delete "))) {
                throw new Exception("Only select, insert, update and delete statements are supported.");
            }
            if (statement.startsWith("select ")) {
                selectStatementCount++;
            } else {
                nonSelectStatementExists = true;
            }
        }
        if (selectStatementCount > 1) {
            throw new Exception("At most one select statement is supported.");
        }
        if (selectStatementCount == 1 && nonSelectStatementExists) {
            throw new Exception("Mixture of select and non-select statements are not supported.");
        }
    }
}
