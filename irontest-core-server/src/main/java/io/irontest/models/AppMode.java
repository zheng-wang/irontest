package io.irontest.models;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Zheng on 6/12/2017.
 */
public enum AppMode {
    LOCAL("local"), TEAM("team");

    private final String text;

    private AppMode(String text) {
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
