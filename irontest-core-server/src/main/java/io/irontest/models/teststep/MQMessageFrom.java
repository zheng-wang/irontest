package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Zheng on 19/03/2017.
 */
public enum MQMessageFrom {
    TEXT("Text"), FILE("File");

    private final String text;

    private MQMessageFrom(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static MQMessageFrom getByText(String text) {
        for (MQMessageFrom e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
