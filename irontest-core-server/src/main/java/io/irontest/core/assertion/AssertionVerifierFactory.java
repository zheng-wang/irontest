package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.JSONValidAgainstJSONSchemaAssertionProperties;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AssertionVerifierFactory {
    private static AssertionVerifierFactory instance = new AssertionVerifierFactory();

    private AssertionVerifierFactory() { }

    public static AssertionVerifierFactory getInstance() {
        return instance;
    }

    /**
     * This method modifies content of the assertion object.
     * @param assertion
     * @param referenceableStringProperties
     * @return
     * @throws IOException
     */
    public AssertionVerifier create(Assertion assertion, Map<String, String> referenceableStringProperties) throws IOException {
        AssertionVerifier result;
        String assertionType = assertion.getType();

        switch (assertionType) {
            case Assertion.TYPE_STATUS_CODE_EQUAL:
                result = new StatusCodeEqualAssertionVerifier();
                break;
            case Assertion.TYPE_XPATH:
                result = new XPathAssertionVerifier();
                break;
            case Assertion.TYPE_CONTAINS:
                result = new ContainsAssertionVerifier();
                break;
            case Assertion.TYPE_TEXT_EQUAL:
                result = new TextEqualAssertionVerifier();
                break;
            case Assertion.TYPE_SUBSTRING:
                result = new SubstringAssertionVerifier();
                break;
            case Assertion.TYPE_REGEX_MATCH:
                result = new RegexMatchAssertionVerifier();
                break;
            case Assertion.TYPE_INTEGER_EQUAL:
                result = new IntegerEqualAssertionVerifier();
                break;
            case Assertion.TYPE_XML_EQUAL:
                result = new XMLEqualAssertionVerifier();
                break;
            case Assertion.TYPE_XML_VALID_AGAINST_XSD:
                result = new XMLValidAgainstXSDAssertionVerifier();
                break;
            case Assertion.TYPE_JSONPATH:
                result = new JSONPathAssertionVerifier();
                break;
            case Assertion.TYPE_JSONPATH_XMLEQUAL:
                result = new JSONPathXMLEqualAssertionVerifier();
                break;
            case Assertion.TYPE_JSON_EQUAL:
                result = new JSONEqualAssertionVerifier();
                break;
            case Assertion.TYPE_JSON_VALID_AGAINST_JSON_SCHEMA:
                result = new JSONValidAgainstJSONSchemaAssertionVerifier();
                break;
            case Assertion.TYPE_HTTP_STUB_HIT:
                result = new HTTPStubHitAssertionVerifier();
                break;
            case Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED:
                result = new AllHTTPStubRequestsMatchedAssertionVerifier();
                break;

            case Assertion.TYPE_HTTP_STUBS_HIT_IN_ORDER:
                result = new HTTPStubsHitInOrderAssertionVerifier();
                break;
            case Assertion.TYPE_HAS_AN_MQRFH2_FOLDER_EQUAL_TO_XML:
                result = new HasAnMQRFH2FolderEqualToXmlAssertionVerifier();
                break;
            default:
                throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }

        resolveStringPropertyReferences(assertion, referenceableStringProperties);

        result.setAssertion(assertion);

        return result;
    }

    private void resolveStringPropertyReferences(Assertion assertion, Map<String, String> referenceableStringProperties) throws IOException {
        MapValueLookup stringPropertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);

        //  resolve string property references in assertion.name
        String resolvedAssertionName = new StrSubstitutor(stringPropertyReferenceResolver)
                .replace(assertion.getName());
        assertion.setName(resolvedAssertionName);
        Set<String> undefinedStringProperties = stringPropertyReferenceResolver.getUnfoundKeys();

        //  resolve string property references in assertion.otherProperties
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        String assertionOtherPropertiesJSON =  objectMapper.writeValueAsString(assertion.getOtherProperties());
        String resolvedAssertionOtherPropertiesJSON = new StrSubstitutor(stringPropertyReferenceResolver)
                .replace(assertionOtherPropertiesJSON);
        undefinedStringProperties.addAll(stringPropertyReferenceResolver.getUnfoundKeys());
        String tempAssertionJSON = "{\"type\":\"" + assertion.getType() + "\",\"otherProperties\":" +
                resolvedAssertionOtherPropertiesJSON + "}";
        Assertion tempAssertion = objectMapper.readValue(tempAssertionJSON, Assertion.class);
        assertion.setOtherProperties(tempAssertion.getOtherProperties());

        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }
    }
}
