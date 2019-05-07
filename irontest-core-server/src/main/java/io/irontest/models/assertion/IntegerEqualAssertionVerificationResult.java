package io.irontest.models.assertion;

public class IntegerEqualAssertionVerificationResult extends AssertionVerificationResult {
    private int actualNumber;

    public int getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(int actualNumber) {
        this.actualNumber = actualNumber;
    }
}
