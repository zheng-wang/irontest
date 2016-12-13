package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONPathAssertionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionVerifier implements AssertionVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONPathAssertionVerifier.class);

    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        JSONPathAssertionProperties otherProperties =
                (JSONPathAssertionProperties) assertion.getOtherProperties();
        String inputJSON = null;
        try {
            inputJSON = new ObjectMapper().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to verify JSONPathAssertion.", e);
            result.setError(e.getMessage());
        }
        result.setResult(result.getError() == null &&
                otherProperties.getExpectedValue().equals(JsonPath.read(inputJSON, otherProperties.getJsonPath())) ?
                TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
