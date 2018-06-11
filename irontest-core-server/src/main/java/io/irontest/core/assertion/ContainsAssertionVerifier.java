package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.ContainsAssertionProperties;
import org.apache.commons.lang3.StringUtils;

public class ContainsAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param input the String that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception {
        AssertionVerificationResult result = new AssertionVerificationResult();
        ContainsAssertionProperties otherProperties =
                (ContainsAssertionProperties) assertion.getOtherProperties();

        //  validate other properties
        if ("".equals(StringUtils.trimToEmpty(otherProperties.getContains()))) {
            throw new IllegalArgumentException("Contains not specified");
        }

        String inputStr = (String) input;
        result.setResult(inputStr.contains(otherProperties.getContains()) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
