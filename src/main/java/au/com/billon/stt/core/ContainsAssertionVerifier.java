package au.com.billon.stt.core;

import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.AssertionVerification;
import au.com.billon.stt.models.ContainsAssertionProperties;

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
