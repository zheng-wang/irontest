package io.irontest.models;

/**
 * Created by zhenw9 on 10/06/2016.
 */
public class WaitTeststepProperties extends Properties {
    private int seconds;

    public WaitTeststepProperties() {}

    public WaitTeststepProperties(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
