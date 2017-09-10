package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.UDPValueLookup;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import org.apache.commons.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 6/08/2015.
 */
public abstract class AssertionVerifier {
    private List<UserDefinedProperty> testcaseUDPs;

    /**
     * @param assertion the assertion to be verified (against the input)
     * @param input the object that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        //  resolve UDP references in assertion.otherProperties
        final List<String> undefinedProperties = new ArrayList<String>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        String assertionOtherPropertiesJSON =  objectMapper.writeValueAsString(assertion.getOtherProperties());
        UDPValueLookup propertyReferenceResolver = new UDPValueLookup(this.testcaseUDPs, true);
        String resolvedAssertionOtherPropertiesJSON = new StrSubstitutor(propertyReferenceResolver)
                .replace(assertionOtherPropertiesJSON);
        undefinedProperties.addAll(propertyReferenceResolver.getUndefinedProperties());
        String tempAssertionJSON = "{\"type\":\"" + assertion.getType() + "\",\"otherProperties\":" +
                resolvedAssertionOtherPropertiesJSON + "}";
        Assertion tempAssertion = objectMapper.readValue(tempAssertionJSON, Assertion.class);
        assertion.setOtherProperties(tempAssertion.getOtherProperties());
        if (!undefinedProperties.isEmpty()) {
            throw new RuntimeException("Properties " + undefinedProperties + " are undefined.");
        }

        return _verify(assertion, input);
    }

    protected void setTestcaseUDPs(List<UserDefinedProperty> testcaseUDPs) {
        this.testcaseUDPs = testcaseUDPs;
    }

    public abstract AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception;
}
