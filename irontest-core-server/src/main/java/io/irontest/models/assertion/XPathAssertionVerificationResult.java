package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.JsonViews;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XPathAssertionVerificationResult extends AssertionVerificationResult {
    @JsonView(JsonViews.TestcaseRun.class)
    private String actualValue;

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
