package io.irontest.models.assertion;

public class AssertionVerification {
    private Assertion assertion;
    private AssertionVerificationResult verificationResult;

    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    public AssertionVerificationResult getVerificationResult() {
        return verificationResult;
    }

    public void setVerificationResult(AssertionVerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }
}
