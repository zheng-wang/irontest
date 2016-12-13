package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONPathAssertionProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by zhenw9 on 13/12/2016.
 */
public class JSONPathAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        AssertionVerificationResult result = new AssertionVerificationResult();
//        result.setResult(TestResult.PASSED);
        JSONPathAssertionProperties otherProperties =
                (JSONPathAssertionProperties) assertion.getOtherProperties();
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) input;

        return result;
    }
}
