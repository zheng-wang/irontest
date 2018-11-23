package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;

import java.util.Map;

public class AssertionVerifierFactory {
    private static AssertionVerifierFactory instance = new AssertionVerifierFactory();

    private AssertionVerifierFactory() { }

    public static AssertionVerifierFactory getInstance() {
        return instance;
    }

    public AssertionVerifier create(String assertionType, Map<String, String> referenceableStringProperties) {
        AssertionVerifier result;
        if (Assertion.TYPE_STATUS_CODE_EQUAL.equals(assertionType)) {
            result = new StatusCodeEqualAssertionVerifier();
        } else if (Assertion.TYPE_XPATH.equals(assertionType)) {
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
        } else if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertionType)) {
            result = new HTTPStubHitAssertionVerifier();
        } else if (Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED.equals(assertionType)) {
            result = new AllHTTPStubRequestsMatchedAssertionVerifier();
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }

        result.setReferenceableStringProperties(referenceableStringProperties);

        return result;
    }
}
