package io.irontest.core.assertion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONPathAssertionProperties;
import io.irontest.models.assertion.JSONPathAssertionVerificationResult;
import org.apache.commons.lang3.StringUtils;

public class JSONPathAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the JSON string that the assertion is verified against
     * @return
     * @throws Exception
     */
    @Override
    public AssertionVerificationResult verify(Object ...inputs) throws Exception {
        JSONPathAssertionProperties otherProperties =
                (JSONPathAssertionProperties) getAssertion().getOtherProperties();

        //  validate other properties
        if ("".equals(StringUtils.trimToEmpty(otherProperties.getJsonPath()))) {
            throw new IllegalArgumentException("JSONPath not specified");
        } else if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValueJSON()))) {
            throw new IllegalArgumentException("Expected Value not specified");
        }

        JSONPathAssertionVerificationResult result = new JSONPathAssertionVerificationResult();
        ObjectMapper objectMapper = new ObjectMapper();
        Object expectedValue = objectMapper.readValue(otherProperties.getExpectedValueJSON(), Object.class);
        Object actualValue = JsonPath.read((String) inputs[0], otherProperties.getJsonPath());
        result.setActualValueJSON(objectMapper.writeValueAsString(actualValue));
        result.setResult(expectedValue.equals(actualValue) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
