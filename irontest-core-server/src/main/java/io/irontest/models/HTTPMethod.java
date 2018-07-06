package io.irontest.models;

public enum HTTPMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    private final String text;

    HTTPMethod(String text) {
        this.text = text;
    }
}
