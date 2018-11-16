package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.apache.commons.text.StrSubstitutor;

import java.util.Map;
import java.util.Set;

public abstract class AssertionVerifier {
    private Map<String, String> referenceableStringProperties;

    /**
     * This method modifies the content of assertion object.
     * @param assertion the assertion to be verified (against the input)
     * @param inputs the objects that the assertion is verified against.
     * @return
     * @throws Exception
     */
    public AssertionVerificationResult verify(Assertion assertion, Object ...inputs) throws Exception {
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

        return _verify(assertion, inputs);
    }

    protected void setReferenceableStringProperties(Map<String, String> referenceableStringProperties) {
        this.referenceableStringProperties = referenceableStringProperties;
    }

    public abstract AssertionVerificationResult _verify(Assertion assertion, Object ...inputs) throws Exception;
}
