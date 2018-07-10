package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.StatusCodeEqualAssertionProperties;

public class StatusCodeEqualAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object statusCode) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        StatusCodeEqualAssertionProperties assertionProperties = (StatusCodeEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties.getStatusCode() == null) {
            throw new IllegalArgumentException("Expected status code is not specified.");
        } else if (statusCode == null) {
            throw new IllegalArgumentException("Actual status code is null.");
        }

        result.setResult(assertionProperties.getStatusCode().equals(statusCode) ?
                TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
