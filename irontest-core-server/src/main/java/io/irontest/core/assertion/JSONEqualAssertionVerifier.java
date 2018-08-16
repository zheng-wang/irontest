package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParseException;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONEqualAssertionProperties;
import io.irontest.models.assertion.MessageEqualAssertionVerificationResult;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

public class JSONEqualAssertionVerifier extends AssertionVerifier {
    private static final String JSON_UNIT_PLACEHOLDER_REGEX = "#\\{[\\s]*(json-unit\\.[^}]+)}";
    private static final String JSON_UNIT_PLACEHOLDER_DELIMITER_REPLACEMENT = "\\${$1}";

    /**
     *
     * @param assertion
     * @param input the JSON string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object input) {
        JSONEqualAssertionProperties assertionProperties = (JSONEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties.getExpectedJSON() == null) {
            throw new IllegalArgumentException("Expected JSON is null.");
        } else if (input == null) {
            throw new IllegalArgumentException("Actual JSON is null.");
        } else if (input.equals("")) {
            throw new IllegalArgumentException("Actual JSON is empty.");
        }

        String expectedJSON = assertionProperties.getExpectedJSON().replaceAll(
                JSON_UNIT_PLACEHOLDER_REGEX, JSON_UNIT_PLACEHOLDER_DELIMITER_REPLACEMENT);
        MessageEqualAssertionVerificationResult result = new MessageEqualAssertionVerificationResult();
        try {
            assertJsonEquals(expectedJSON, input);
        } catch (IllegalArgumentException e) {
            Throwable c = e.getCause();
            if (c instanceof JsonParseException) {
                throw new IllegalArgumentException(e.getMessage() + " " + c.getMessage());
            } else {
                throw e;
            }
        } catch (AssertionError ae) {
            result.setDifferences(ae.getMessage());
        }

        if (result.getDifferences() == null) {
            result.setResult(TestResult.PASSED);
        } else {
            result.setResult(TestResult.FAILED);
        }

        return result;
    }
}
