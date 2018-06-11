package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TeststepRequestType {
    TEXT("Text"), FILE("File");

    private final String text;

    TeststepRequestType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static TeststepRequestType getByText(String text) {
        for (TeststepRequestType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
