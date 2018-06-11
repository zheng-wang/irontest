package io.irontest.models.assertion;

public class MessageEqualAssertionVerificationResult extends AssertionVerificationResult {
    private String differences;

    public String getDifferences() {
        return differences;
    }

    public void setDifferences(String differences) {
        this.differences = differences;
    }
}
