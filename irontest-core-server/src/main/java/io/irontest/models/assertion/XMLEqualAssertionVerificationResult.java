package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.JsonViews;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerificationResult extends AssertionVerificationResult {
    @JsonView(JsonViews.TestcaseRun.class)
    private String differences;

    public String getDifferences() {
        return differences;
    }

    public void setDifferences(String differences) {
        this.differences = differences;
    }
}
