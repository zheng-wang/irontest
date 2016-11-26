package io.irontest.core.runner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 10/04/2016.
 */
public class DBAPIResponse {
    //  applicable only to non-select statements
    private List<StatementExecutionResult> statementExecutionResults = new ArrayList<StatementExecutionResult>();
    //  applicable only to select statement
    private Object resultSet;

    public DBAPIResponse() { }

    public List<StatementExecutionResult> getStatementExecutionResults() {
        return statementExecutionResults;
    }

    public void setStatementExecutionResults(List<StatementExecutionResult> statementExecutionResults) {
        this.statementExecutionResults = statementExecutionResults;
    }

    public Object getResultSet() {
        return resultSet;
    }

    public void setResultSet(Object resultSet) {
        this.resultSet = resultSet;
    }
}
