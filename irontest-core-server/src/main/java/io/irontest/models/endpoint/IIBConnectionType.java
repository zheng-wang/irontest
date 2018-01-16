package io.irontest.models.endpoint;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Zheng on 16/01/2018.
 */
public enum IIBConnectionType {
    LOCAL("Local"), REMOTE("Remote");

    private final String text;

    IIBConnectionType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static IIBConnectionType getByText(String text) {
        for (IIBConnectionType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
