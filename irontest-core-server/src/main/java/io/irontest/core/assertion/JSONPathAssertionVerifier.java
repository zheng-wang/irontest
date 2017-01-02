package io.irontest.core.assertion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONPathAssertionProperties;
import io.irontest.models.assertion.JSONPathAssertionVerificationResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionVerifier implements AssertionVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONPathAssertionVerifier.class);

    /**
     * @param assertion the assertion to be verified (against the input)
     * @param input the JSON string that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        JSONPathAssertionVerificationResult result = new JSONPathAssertionVerificationResult();
        JSONPathAssertionProperties otherProperties =
                (JSONPathAssertionProperties) assertion.getOtherProperties();
        try {
            if ("".equals(StringUtils.trimToEmpty(otherProperties.getJsonPath()))) {
                throw new IllegalArgumentException("JSONPath not specified");
            }
            if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValueJSON()))) {
                throw new IllegalArgumentException("Expected Value not specified");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Object expectedValue = objectMapper.readValue(otherProperties.getExpectedValueJSON(), Object.class);
            Object actualValue = JsonPath.read((String) input, otherProperties.getJsonPath());
            result.setActualValueJSON(objectMapper.writeValueAsString(actualValue));
            boolean equal = expectedValue == null ? actualValue == null : expectedValue.equals(actualValue);
            result.setResult(equal ? TestResult.PASSED : TestResult.FAILED);
        } catch (Exception e) {
            LOGGER.error("Failed to verify JSONPathAssertion.", e);
            result.setError(e.getMessage());
            result.setResult(TestResult.FAILED);
        }

        return result;
    }
}
