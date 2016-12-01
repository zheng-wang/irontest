package io.irontest.core.runner;

import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 10/04/2016.
 */
public class DBAPIResponse {
    //  applicable only to non-select statements
    private String statementExecutionResults;
    //  applicable only to select statement
    private List<Map<String, Object>> resultSet;

    public DBAPIResponse() { }

    public String getStatementExecutionResults() {
        return statementExecutionResults;
    }

    public void setStatementExecutionResults(String statementExecutionResults) {
        this.statementExecutionResults = statementExecutionResults;
    }

    public List<Map<String, Object>> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<Map<String, Object>> resultSet) {
        this.resultSet = resultSet;
    }
}
