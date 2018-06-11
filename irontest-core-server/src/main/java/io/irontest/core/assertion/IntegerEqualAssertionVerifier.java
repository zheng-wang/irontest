package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;

public class IntegerEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param input the Integer that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception {
        AssertionVerificationResult result = new AssertionVerificationResult();
        IntegerEqualAssertionProperties properties = (IntegerEqualAssertionProperties)
                assertion.getOtherProperties();
        result.setResult(new Integer(properties.getNumber()).equals(input) ? TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
