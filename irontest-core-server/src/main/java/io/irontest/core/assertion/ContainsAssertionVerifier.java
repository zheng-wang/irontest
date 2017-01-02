package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.ContainsAssertionProperties;

/**
 * Created by Zheng on 6/08/2015.
 */
public class ContainsAssertionVerifier implements AssertionVerifier {
    /**
     *
     * @param assertion
     * @param input the String that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        ContainsAssertionProperties otherProperties =
                (ContainsAssertionProperties) assertion.getOtherProperties();

        //  validate other properties
        if (otherProperties == null) {
            result.setError("Contains not specified");
            result.setResult(TestResult.FAILED);
            return result;
        }

        String inputStr = (String) input;
        result.setResult(inputStr.contains(otherProperties.getContains()) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
