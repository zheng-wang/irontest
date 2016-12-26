package io.irontest.core.assertion;

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
        try {
            String inputJSON = new ObjectMapper().writeValueAsString(input);
            Object value = JsonPath.read(inputJSON, otherProperties.getJsonPath());

            //Object expectedValueObj = JsonPath.read((String) otherProperties.getExpectedValue(), "$");
            result.setResult(otherProperties.getExpectedValue().equals(value) ?
                    TestResult.PASSED : TestResult.FAILED);
        } catch (Exception e) {
            LOGGER.error("Failed to verify JSONPathAssertion.", e);
            result.setError(e.getMessage());
            result.setResult(TestResult.FAILED);
        }

        return result;
    }
}
