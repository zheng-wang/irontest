package io.irontest.core.runner;

/**
 * numberOfRowsModified being -1 means the SQL is a select statement;
 * > -1 means the SQL is an insert/update/delete statement.
 * Created by Zheng on 10/04/2016.
 */
public class DBAPIResponse {
    private int numberOfRowsModified;
    private Object resultSet;

    public DBAPIResponse() { }

    public int getNumberOfRowsModified() {
        return numberOfRowsModified;
    }

    public void setNumberOfRowsModified(int numberOfRowsModified) {
        this.numberOfRowsModified = numberOfRowsModified;
    }

    public Object getResultSet() {
        return resultSet;
    }

    public void setResultSet(Object resultSet) {
        this.resultSet = resultSet;
    }
}
