package io.irontest.core.teststep;

import java.util.List;

public class DBAPIResponse extends APIResponse {
    //  applicable only to non-select statements
    private String statementExecutionResults;

    //  applicable only to select statement; column names are of lower case
    private List<String> columnNames;
    private String rowsJSON;

    public DBAPIResponse() { }

    public String getStatementExecutionResults() {
        return statementExecutionResults;
    }

    public void setStatementExecutionResults(String statementExecutionResults) {
        this.statementExecutionResults = statementExecutionResults;
    }

    public String getRowsJSON() {
        return rowsJSON;
    }

    public void setRowsJSON(String rowsJSON) {
        this.rowsJSON = rowsJSON;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
}
