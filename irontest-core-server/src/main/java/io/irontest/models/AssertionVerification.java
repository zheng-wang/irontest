package io.irontest.models;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;

/**
 * Created by Zheng on 25/07/2016.
 */
public class AssertionVerification {
    private Assertion assertion;
    private AssertionVerificationResult assertionVerificationResult;

    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    public AssertionVerificationResult getAssertionVerificationResult() {
        return assertionVerificationResult;
    }

    public void setAssertionVerificationResult(AssertionVerificationResult assertionVerificationResult) {
        this.assertionVerificationResult = assertionVerificationResult;
    }
}
