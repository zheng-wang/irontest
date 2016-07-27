package io.irontest.models.assertion;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XPathAssertionVerificationResult extends AssertionVerificationResult {
    private String actualValue;

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
