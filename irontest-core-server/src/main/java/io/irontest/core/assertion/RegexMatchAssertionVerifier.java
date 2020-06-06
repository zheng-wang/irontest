package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.RegexMatchAssertionProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class RegexMatchAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        String inputStr = (String) inputs[0];
        RegexMatchAssertionProperties assertionProperties =
                (RegexMatchAssertionProperties) getAssertion().getOtherProperties();
        String regex = StringUtils.trimToEmpty(assertionProperties.getRegex());

        //  validate arguments
        if (inputStr == null) {
            throw new IllegalArgumentException("Input string is null");
        } else if ("".equals(regex)) {
            throw new IllegalArgumentException("Regex not specified");
        }

        AssertionVerificationResult result = new AssertionVerificationResult();
        result.setResult(Pattern.matches(regex, inputStr) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
