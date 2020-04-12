package io.irontest.models.assertion;

public class XMLValidAgainstXSDAssertionVerificationResult extends AssertionVerificationResult {
    private String failureDetails;     //  details about how the XML is not conforming to the XSD

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }
}
