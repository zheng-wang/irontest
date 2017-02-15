package io.irontest.core.assertion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONPathAssertionProperties;
import io.irontest.models.assertion.JSONPathAssertionVerificationResult;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionVerifier implements AssertionVerifier {
    /**
     * @param assertion the assertion to be verified (against the input)
     * @param input the JSON string that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        JSONPathAssertionProperties otherProperties =
                (JSONPathAssertionProperties) assertion.getOtherProperties();

        //  validate other properties
        if (otherProperties == null || "".equals(StringUtils.trimToEmpty(otherProperties.getJsonPath()))) {
            throw new IllegalArgumentException("JSONPath not specified");
        } else if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValueJSON()))) {
            throw new IllegalArgumentException("Expected Value not specified");
        }

        JSONPathAssertionVerificationResult result = new JSONPathAssertionVerificationResult();
        ObjectMapper objectMapper = new ObjectMapper();
        Object expectedValue = objectMapper.readValue(otherProperties.getExpectedValueJSON(), Object.class);
        Object actualValue = JsonPath.read((String) input, otherProperties.getJsonPath());
        result.setActualValueJSON(objectMapper.writeValueAsString(actualValue));
        result.setResult(expectedValue.equals(actualValue) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
