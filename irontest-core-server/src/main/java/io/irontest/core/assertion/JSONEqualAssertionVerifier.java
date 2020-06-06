package io.irontest.core.assertion;

import com.fasterxml.jackson.core.JsonParseException;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONEqualAssertionProperties;
import io.irontest.models.assertion.MessageEqualAssertionVerificationResult;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

public class JSONEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the JSON string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object ...inputs) {
        JSONEqualAssertionProperties assertionProperties = (JSONEqualAssertionProperties) getAssertion().getOtherProperties();
        String expectedJSON = assertionProperties.getExpectedJSON();

        //  validate arguments
        if (expectedJSON == null) {
            throw new IllegalArgumentException("Expected JSON is null.");
        } else if (inputs[0] == null) {
            throw new IllegalArgumentException("Actual JSON is null.");
        } else if (inputs[0].equals("")) {
            throw new IllegalArgumentException("Actual JSON is empty.");
        }

        MessageEqualAssertionVerificationResult result = new MessageEqualAssertionVerificationResult();
        try {
            assertJsonEquals(expectedJSON, inputs[0]);
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
