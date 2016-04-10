package io.irontest.core;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.ContainsAssertionProperties;

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
