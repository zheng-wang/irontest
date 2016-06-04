package io.irontest.core.assertion;

import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.IntegerEqualsAssertionProperties;

/**
 * Created by Zheng on 4/06/2016.
 */
public class IntegerEqualsAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        IntegerEqualsAssertionProperties properties = (IntegerEqualsAssertionProperties)
                assertionVerification.getAssertion().getOtherProperties();
        result.setPassed(new Integer(properties.getNumber()).equals(assertionVerification.getInput()));

        return result;
    }
}
