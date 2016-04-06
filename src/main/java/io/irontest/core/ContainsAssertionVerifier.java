package io.irontest.core;

import io.irontest.models.Assertion;
import io.irontest.models.AssertionVerification;
import io.irontest.models.ContainsAssertionProperties;

/**
 * Created by Zheng on 6/08/2015.
 */
public class ContainsAssertionVerifier implements AssertionVerifier {
    public Assertion verify(Assertion assertion) {
        ContainsAssertionProperties properties = (ContainsAssertionProperties) assertion.getProperties();
        AssertionVerification verification = assertion.getVerification();
        verification.setPassed(verification.getInput().contains(properties.getContains()));
        return assertion;
    }
}
