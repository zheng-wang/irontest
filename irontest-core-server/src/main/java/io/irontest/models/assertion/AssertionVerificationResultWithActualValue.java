package io.irontest.models.assertion;

public class AssertionVerificationResultWithActualValue extends AssertionVerificationResult {
    private String actualValue;

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
