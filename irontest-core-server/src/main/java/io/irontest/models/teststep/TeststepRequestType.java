package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Zheng on 2/09/2017.
 */
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
