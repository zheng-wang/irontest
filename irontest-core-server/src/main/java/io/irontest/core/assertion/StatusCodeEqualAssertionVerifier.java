package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResultWithActualValue;
import io.irontest.models.assertion.StatusCodeEqualAssertionProperties;

public class StatusCodeEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: statusCode (must be an integer)
     * @return
     */
    @Override
    public AssertionVerificationResultWithActualValue verify(Object ...inputs) {
        AssertionVerificationResultWithActualValue result = new AssertionVerificationResultWithActualValue();
        StatusCodeEqualAssertionProperties assertionProperties =
                (StatusCodeEqualAssertionProperties) getAssertion().getOtherProperties();

        //  validate arguments
        if (assertionProperties.getStatusCode() == null) {
            throw new IllegalArgumentException("Expected status code is not specified.");
        } else if (inputs[0] == null) {
            throw new IllegalArgumentException("Actual status code is null.");
        }

        String statusCodeStr = ((Integer) inputs[0]).toString();
        result.setActualValue(statusCodeStr);
        result.setResult(assertionProperties.getStatusCode().equals(statusCodeStr) ?
                TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
