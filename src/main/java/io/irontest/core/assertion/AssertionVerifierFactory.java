package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;

/**
 * Created by Zheng on 6/08/2015.
 */
public class AssertionVerifierFactory {
    public AssertionVerifier create(String assertionType) {
        AssertionVerifier result = null;
        if (Assertion.ASSERTION_TYPE_XPATH.equals(assertionType)) {
            result = new XPathAssertionVerifier();
        } else if (Assertion.ASSERTION_TYPE_CONTAINS.equals(assertionType)) {
            result = new ContainsAssertionVerifier();
        } else if (Assertion.ASSERTION_TYPE_DSFIELD.equals(assertionType)) {
            result = new DSFieldAssertionVerifier();
        } else if (Assertion.ASSERTION_TYPE_INTEGER_EQUAL.equals(assertionType)) {
            result = new IntegerEqualAssertionVerifier();
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }

        return result;
    }
}
