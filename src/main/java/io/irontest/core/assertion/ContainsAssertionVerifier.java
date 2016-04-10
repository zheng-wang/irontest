package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.ContainsAssertionProperties;

/**
 * Created by Zheng on 6/08/2015.
 */
public class ContainsAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        ContainsAssertionProperties properties =
                (ContainsAssertionProperties) assertionVerification.getAssertion().getProperties();
        String input = (String) assertionVerification.getInput();
        result.setPassed(input.contains(properties.getContains()));
        return result;
    }
}
