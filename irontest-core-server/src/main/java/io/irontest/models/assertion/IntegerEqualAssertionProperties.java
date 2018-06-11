package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class IntegerEqualAssertionProperties extends Properties {
    private int number;

    public IntegerEqualAssertionProperties() {}

    public IntegerEqualAssertionProperties(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
