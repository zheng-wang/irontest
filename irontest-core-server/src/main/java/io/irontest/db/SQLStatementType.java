package io.irontest.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SQLStatementType {
    SELECT("SELECT"), INSERT("INSERT"), UPDATE("UPDATE"), DELETE("DELETE");

    private final String text;

    SQLStatementType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonIgnore
    public static boolean isSelectStatement(String statement) {
        return statement.toUpperCase().startsWith(SELECT + " ");
    }

    @JsonIgnore
    public static boolean isInsertStatement(String statement) {
        return statement.toUpperCase().startsWith(INSERT + " ");
    }

    @JsonIgnore
    public static boolean isUpdateStatement(String statement) {
        return statement.toUpperCase().startsWith(UPDATE + " ");
    }

    @JsonIgnore
    public static boolean isDeleteStatement(String statement) {
        return statement.toUpperCase().startsWith(DELETE + " ");
    }

    public static SQLStatementType getByStatement(String statement) {
        SQLStatementType result = null;
        for (SQLStatementType type: values()) {
            if (statement.toUpperCase().startsWith(type + " ")) {
                result = type;
                break;
            }
        }
        return result;
    }
}
