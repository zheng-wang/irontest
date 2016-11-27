package io.irontest.core.runner;

/**
 * Created by Zheng on 10/04/2016.
 */
public class DBAPIResponse {
    //  applicable only to non-select statements
    private String statementExecutionResults;
    //  applicable only to select statement
    private Object resultSet;

    public DBAPIResponse() { }

    public String getStatementExecutionResults() {
        return statementExecutionResults;
    }

    public void setStatementExecutionResults(String statementExecutionResults) {
        this.statementExecutionResults = statementExecutionResults;
    }

    public Object getResultSet() {
        return resultSet;
    }

    public void setResultSet(Object resultSet) {
        this.resultSet = resultSet;
    }
}
