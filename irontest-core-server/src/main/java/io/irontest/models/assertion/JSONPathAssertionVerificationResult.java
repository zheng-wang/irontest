package io.irontest.models.assertion;

/**
 * Created by Zheng on 27/12/2016.
 */
public class JSONPathAssertionVerificationResult extends AssertionVerificationResult {
    private Object actualValue;

    public Object getActualValue() {
        return actualValue;
    }

    public void setActualValue(Object actualValue) {
        this.actualValue = actualValue;
    }
}
