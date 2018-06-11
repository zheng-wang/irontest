package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DataTableColumnType {
    STRING("String"), DBENDPOINT("DBEndpoint");

    private final String text;

    DataTableColumnType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static DataTableColumnType getByText(String text) {
        for (DataTableColumnType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
