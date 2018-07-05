package io.irontest.models;

public enum HTTPMethod {
    POST("POST");

    private final String text;

    HTTPMethod(String text) {
        this.text = text;
    }
}
