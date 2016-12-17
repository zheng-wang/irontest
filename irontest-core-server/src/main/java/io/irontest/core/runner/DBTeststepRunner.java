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
            //  parse the SQL script
            script = handle.createScript(request);
            //  replace below code with statements = script.getStatements() after upgrading JDBI to a release after 2016.11.26
            Method method = script.getClass().getDeclaredMethod("getStatements");
            method.setAccessible(true);
            statements = (List<String>) method.invoke(script);
        }
        sanityCheckTheStatements(statements);

        if (SQLStatementType.isSelectStatement(statements.get(0))) {    //  the request is a select statement
            MetadataResultSetMapper mapper = new MetadataResultSetMapper();
            //  use statements.get(0) instead of the raw request, as Oracle does not support trailing semicolon in select statement
            Query<Map<String, Object>> query = handle.createQuery(statements.get(0)).map(mapper);
            List<Map<String, Object>> rows = query.list();
            response.setRows(rows);
        } else {                                          //  the request is one or more non-select statements
            int[] returnValues = script.execute();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < returnValues.length; i++) {
                String statementType = SQLStatementType.getByStatement(statements.get(i)).toString();
                sb.append(returnValues[i]).append(" row(s) ").append(statementType.toLowerCase())
                    .append(statementType.endsWith("E") ? "d" : "ed").append("\n");
                response.setStatementExecutionResults(sb.toString());
            }
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
            if (!(SQLStatementType.isSelectStatement(statement) ||
                    SQLStatementType.isInsertStatement(statement) ||
                    SQLStatementType.isUpdateStatement(statement) ||
                    SQLStatementType.isDeleteStatement(statement))) {
                throw new Exception("Only " + SQLStatementType.SELECT + ", " + SQLStatementType.INSERT + ", " +
                        SQLStatementType.UPDATE + " and " + SQLStatementType.DELETE + " statements are supported.");
            }
            if (SQLStatementType.isSelectStatement(statement)) {
                selectStatementCount++;
            } else {
                nonSelectStatementExists = true;
            }
        }
        if (selectStatementCount > 1) {
            throw new Exception("At most one " + SQLStatementType.SELECT + " statement is supported.");
        }
        if (selectStatementCount == 1 && nonSelectStatementExists) {
            throw new Exception("Mixture of " + SQLStatementType.SELECT + " and non-" + SQLStatementType.SELECT + " statements are not supported.");
        }
    }
}
