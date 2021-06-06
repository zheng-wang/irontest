package io.irontest.models.assertion;

public class JSONValidAgainstJSONSchemaAssertionVerificationResult extends AssertionVerificationResult {
    private String failureDetails;     //  details about how the JSON is not conforming to the JSON Schema

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }
}
