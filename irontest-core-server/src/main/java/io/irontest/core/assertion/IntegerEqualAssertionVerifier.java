package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;

public class IntegerEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param inputs contains only one argument: the Integer that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object ...inputs) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        IntegerEqualAssertionProperties properties = (IntegerEqualAssertionProperties)
                assertion.getOtherProperties();
        result.setResult(Integer.valueOf(properties.getNumber()).equals(inputs[0]) ? TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
