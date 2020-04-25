package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FtpUploadFileFrom {
    TEXT("Text"), FILE("File");

    private final String text;

    FtpUploadFileFrom(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static FtpUploadFileFrom getByText(String text) {
        for (FtpUploadFileFrom e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
