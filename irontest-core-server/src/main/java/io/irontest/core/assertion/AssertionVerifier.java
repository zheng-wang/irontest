package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.apache.commons.text.StrSubstitutor;

import java.util.Map;
import java.util.Set;

/**
 * Created by Zheng on 6/08/2015.
 */
public abstract class AssertionVerifier {
    private Map<String, String> referenceableProperties;

    /**
     * @param assertion the assertion to be verified (against the input)
     * @param input the object that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        MapValueLookup propertyReferenceResolver = new MapValueLookup(referenceableProperties, true);

        //  resolve property references in assertion.name
        String resolvedAssertionName = new StrSubstitutor(propertyReferenceResolver)
                .replace(assertion.getName());
        assertion.setName(resolvedAssertionName);
        Set<String> undefinedProperties = propertyReferenceResolver.getUnfoundKeys();

        //  resolve property references in assertion.otherProperties
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        String assertionOtherPropertiesJSON =  objectMapper.writeValueAsString(assertion.getOtherProperties());
        String resolvedAssertionOtherPropertiesJSON = new StrSubstitutor(propertyReferenceResolver)
                .replace(assertionOtherPropertiesJSON);
        undefinedProperties.addAll(propertyReferenceResolver.getUnfoundKeys());
        String tempAssertionJSON = "{\"type\":\"" + assertion.getType() + "\",\"otherProperties\":" +
                resolvedAssertionOtherPropertiesJSON + "}";
        Assertion tempAssertion = objectMapper.readValue(tempAssertionJSON, Assertion.class);
        assertion.setOtherProperties(tempAssertion.getOtherProperties());

        if (!undefinedProperties.isEmpty()) {
            throw new RuntimeException("Properties " + undefinedProperties + " are undefined.");
        }

        return _verify(assertion, input);
    }

    protected void setReferenceableProperties(Map<String, String> referenceableProperties) {
        this.referenceableProperties = referenceableProperties;
    }

    public abstract AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception;
}
