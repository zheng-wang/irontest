package io.irontest.models.assertion;

public class JSONPathAssertionVerificationResult extends AssertionVerificationResult {
    private String actualValueJSON;

    public String getActualValueJSON() {
        return actualValueJSON;
    }

    public void setActualValueJSON(String actualValueJSON) {
        this.actualValueJSON = actualValueJSON;
    }
}
