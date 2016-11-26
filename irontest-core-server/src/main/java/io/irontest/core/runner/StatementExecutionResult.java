package io.irontest.core.runner;

/**
 * Created by Zheng on 26/11/2016.
 */
public class StatementExecutionResult {
    private SQLStatementType statementType;
    private int returnValue;

    public StatementExecutionResult(SQLStatementType statementType, int returnValue) {
        this.statementType = statementType;
        this.returnValue = returnValue;
    }

    public SQLStatementType getStatementType() {
        return statementType;
    }

    public void setStatementType(SQLStatementType statementType) {
        this.statementType = statementType;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }
}
