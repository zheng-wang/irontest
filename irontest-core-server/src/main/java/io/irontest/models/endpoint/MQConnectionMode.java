package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MQConnectionMode {
    BINDINGS("Bindings"), CLIENT("Client");

    private final String text;

    MQConnectionMode(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static MQConnectionMode getByText(String text) {
        for (MQConnectionMode e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
