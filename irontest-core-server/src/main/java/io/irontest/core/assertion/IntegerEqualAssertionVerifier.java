package io.irontest.core.assertion;

import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.IntegerEqualAssertionProperties;

/**
 * Created by Zheng on 4/06/2016.
 */
public class IntegerEqualAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        IntegerEqualAssertionProperties properties = (IntegerEqualAssertionProperties)
                assertionVerification.getAssertion().getOtherProperties();
        result.setPassed(new Integer(properties.getNumber()).equals(assertionVerification.getInput()));

        return result;
    }
}
