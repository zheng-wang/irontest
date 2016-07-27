package io.irontest.models.assertion;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerificationResult extends AssertionVerificationResult {
    private String differences;

    public String getDifferences() {
        return differences;
    }

    public void setDifferences(String differences) {
        this.differences = differences;
    }
}
