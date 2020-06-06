package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.ContainsAssertionProperties;
import org.apache.commons.lang3.StringUtils;

public class ContainsAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the String that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object ...inputs) {
        ContainsAssertionProperties otherProperties =
                (ContainsAssertionProperties) getAssertion().getOtherProperties();
        String contains = otherProperties.getContains();

        //  validate argument
        if ("".equals(StringUtils.trimToEmpty(contains))) {
            throw new IllegalArgumentException("Contains not specified");
        }

        AssertionVerificationResult result = new AssertionVerificationResult();
        String inputStr = (String) inputs[0];
        result.setResult(inputStr.contains(contains) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
