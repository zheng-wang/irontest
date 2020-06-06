package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.AssertionVerificationResultWithActualValue;
import io.irontest.models.assertion.SubstringAssertionProperties;
import org.apache.commons.lang3.StringUtils;

public class SubstringAssertionVerifier extends AssertionVerifier {

    /**
     *
     * @param inputs contains only one argument: the string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        SubstringAssertionProperties assertionProperties =
                (SubstringAssertionProperties) getAssertion().getOtherProperties();
        String beginIndexStr = StringUtils.trimToEmpty(assertionProperties.getBeginIndex());
        String endIndexStr = StringUtils.trimToEmpty(assertionProperties.getEndIndex());
        String expectedValue = assertionProperties.getExpectedValue();
        String inputStr = (String) inputs[0];
        int beginIndex;
        int endIndex = -1;

        //  validate arguments
        if (inputStr == null) {            //  input string can be empty, but not null
            throw new IllegalArgumentException("Input string is null");
        } else if ("".equals(beginIndexStr)) {
            throw new IllegalArgumentException("Begin Index not specified");
        } else if (expectedValue == null) {       //  expected value can be empty, but not null
            throw new IllegalArgumentException("Expected Value not specified");
        }
        try {
            beginIndex = Integer.valueOf(beginIndexStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Begin Index " + beginIndexStr + " is not an integer");
        }
        if (!"".equals(endIndexStr)) {
            try {
                endIndex = Integer.valueOf(endIndexStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("End Index " + endIndexStr + " is not an integer");
            }
        }

        AssertionVerificationResultWithActualValue result = new AssertionVerificationResultWithActualValue();
        String actualValue;
        if ("".equals(endIndexStr)) {
            actualValue = inputStr.substring(beginIndex);
        } else {
            actualValue = inputStr.substring(beginIndex, endIndex);
        }
        result.setActualValue(actualValue);
        result.setResult(expectedValue.equals(actualValue) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
