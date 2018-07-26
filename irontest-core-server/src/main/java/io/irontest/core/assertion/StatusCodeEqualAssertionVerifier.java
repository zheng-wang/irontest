package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResultWithActualValue;
import io.irontest.models.assertion.StatusCodeEqualAssertionProperties;

public class StatusCodeEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param statusCode must be an integer
     * @return
     */
    @Override
    public AssertionVerificationResultWithActualValue _verify(Assertion assertion, Object statusCode) {
        AssertionVerificationResultWithActualValue result = new AssertionVerificationResultWithActualValue();
        StatusCodeEqualAssertionProperties assertionProperties = (StatusCodeEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties.getStatusCode() == null) {
            throw new IllegalArgumentException("Expected status code is not specified.");
        } else if (statusCode == null) {
            throw new IllegalArgumentException("Actual status code is null.");
        }

        String statusCodeStr = ((Integer) statusCode).toString();
        result.setActualValue(statusCodeStr);
        result.setResult(assertionProperties.getStatusCode().equals(statusCodeStr) ?
                TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
