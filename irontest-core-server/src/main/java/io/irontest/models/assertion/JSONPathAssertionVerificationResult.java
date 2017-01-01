package io.irontest.models.assertion;

/**
 * Created by Zheng on 27/12/2016.
 */
public class JSONPathAssertionVerificationResult extends AssertionVerificationResult {
    private String actualValueJSON;

    public String getActualValueJSON() {
        return actualValueJSON;
    }

    public void setActualValueJSON(String actualValueJSON) {
        this.actualValueJSON = actualValueJSON;
    }
}
