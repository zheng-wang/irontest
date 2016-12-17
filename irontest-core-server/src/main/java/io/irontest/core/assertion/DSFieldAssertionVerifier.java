package io.irontest.core.assertion;

import io.irontest.core.runner.DBAPIResponse;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.DSFieldAssertionProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 10/04/2016.
 */
public class DSFieldAssertionVerifier implements AssertionVerifier {
    public static final String CONTAINS_OPERATOR = "Contains";

    /**
     * @param assertion
     * @param input the {@link DBAPIResponse} object that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        result.setResult(TestResult.FAILED);
        DSFieldAssertionProperties assertionProperties = (DSFieldAssertionProperties) assertion.getOtherProperties();
        DBAPIResponse response = (DBAPIResponse) input;
        List<Map<String, Object>> rows = response.getRows();
        if (rows != null && DSFieldAssertionVerifier.CONTAINS_OPERATOR.equals(assertionProperties.getOperator())) {
            for (Map<String, Object> row : rows) {
                if (assertionProperties.getValue().equals(row.get(assertionProperties.getField()))) {
                    result.setResult(TestResult.PASSED);
                    break;
                }
            }
        }

        return result;
    }
}
