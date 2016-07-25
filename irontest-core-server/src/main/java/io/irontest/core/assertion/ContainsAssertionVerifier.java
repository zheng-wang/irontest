package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.ContainsAssertionProperties;

/**
 * Created by Zheng on 6/08/2015.
 */
public class ContainsAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        ContainsAssertionProperties properties =
                (ContainsAssertionProperties) assertion.getOtherProperties();
        String inputStr = (String) input;
        result.setPassed(inputStr.contains(properties.getContains()));
        return result;
    }
}
