package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;

import java.util.Map;

/**
 * Created by Zheng on 6/08/2015.
 */
public class AssertionVerifierFactory {
    private static AssertionVerifierFactory instance = new AssertionVerifierFactory();

    private AssertionVerifierFactory() { }

    public static AssertionVerifierFactory getInstance() {
        return instance;
    }

    public AssertionVerifier create(String assertionType, Map<String, String> referenceableProperties) {
        AssertionVerifier result;
        if (Assertion.TYPE_XPATH.equals(assertionType)) {
            result = new XPathAssertionVerifier();
        } else if (Assertion.TYPE_CONTAINS.equals(assertionType)) {
            result = new ContainsAssertionVerifier();
        } else if (Assertion.TYPE_INTEGER_EQUAL.equals(assertionType)) {
            result = new IntegerEqualAssertionVerifier();
        } else if (Assertion.TYPE_XML_EQUAL.equals(assertionType)) {
            result = new XMLEqualAssertionVerifier();
        } else if (Assertion.TYPE_JSONPATH.equals(assertionType)) {
            result = new JSONPathAssertionVerifier();
        } else if (Assertion.TYPE_JSONPATH_XMLEQUAL.equals(assertionType)){
            result = new JSONPathXMLEqualAssertionVerifier();
        } else if (Assertion.TYPE_JSON_EQUAL.equals(assertionType)) {
            result = new JSONEqualAssertionVerifier();
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }

        result.setReferenceableProperties(referenceableProperties);

        return result;
    }
}
