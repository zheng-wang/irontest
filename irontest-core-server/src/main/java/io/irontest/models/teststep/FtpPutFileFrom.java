package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FtpPutFileFrom {
    TEXT("Text"), FILE("File");

    private final String text;

    FtpPutFileFrom(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static FtpPutFileFrom getByText(String text) {
        for (FtpPutFileFrom e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
