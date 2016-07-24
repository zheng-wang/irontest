package io.irontest.models;

/**
 * Result for running a test step or test case, or for verifying an assertion.
 * Created by Zheng on 24/07/2016.
 */
public enum TestResult {
    PASSED("Passed"), FAILED("Failed");

    private final String text;

    private TestResult(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
