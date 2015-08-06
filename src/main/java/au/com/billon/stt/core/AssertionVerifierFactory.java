package au.com.billon.stt.core;

import au.com.billon.stt.models.Assertion;

/**
 * Created by Zheng on 6/08/2015.
 */
public class AssertionVerifierFactory {
    public AssertionVerifier create(String assertionType) {
        AssertionVerifier result = null;
        if (Assertion.ASSERTION_TYPE_XPATH.equals(assertionType)) {
            result = new XPathAssertionVerifier();
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }

        return result;
    }
}
