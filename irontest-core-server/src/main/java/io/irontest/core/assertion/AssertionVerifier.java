package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 6/08/2015.
 */
public abstract class AssertionVerifier {
    private Map<String, String> implicitProperties;
    private List<UserDefinedProperty> testcaseUDPs;

    /**
     * @param assertion the assertion to be verified (against the input)
     * @param input the object that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        Map<String, String> referenceableProperties = new HashMap<String, String>();
        referenceableProperties.putAll(implicitProperties);
        referenceableProperties.putAll(IronTestUtils.udpListToMap(testcaseUDPs));

        //  resolve property references in assertion.otherProperties
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        String assertionOtherPropertiesJSON =  objectMapper.writeValueAsString(assertion.getOtherProperties());
        MapValueLookup propertyReferenceResolver = new MapValueLookup(referenceableProperties, true);
        String resolvedAssertionOtherPropertiesJSON = new StrSubstitutor(propertyReferenceResolver)
                .replace(assertionOtherPropertiesJSON);
        List<String> undefinedProperties = propertyReferenceResolver.getUnfoundKeys();
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

    protected void setImplicitProperties(Map<String, String> implicitProperties) {
        this.implicitProperties = implicitProperties;
    }

    public abstract AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception;
}
