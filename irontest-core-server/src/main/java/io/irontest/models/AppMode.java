package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AppMode {
    LOCAL("local"), TEAM("team");

    private final String text;

    AppMode(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static AppMode getByText(String text) {
        for (AppMode e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
