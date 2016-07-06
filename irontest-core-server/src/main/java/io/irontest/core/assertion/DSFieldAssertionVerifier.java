package io.irontest.core.assertion;

import io.irontest.core.runner.DBTeststepRunResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.DSFieldAssertionProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 10/04/2016.
 */
public class DSFieldAssertionVerifier implements AssertionVerifier {
    public static final String CONTAINS_OPERATOR = "Contains";

    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        result.setPassed(Boolean.FALSE);
        Assertion assertion = assertionVerification.getAssertion();
        DSFieldAssertionProperties assertionProperties = (DSFieldAssertionProperties) assertion.getOtherProperties();
        DBTeststepRunResult response = (DBTeststepRunResult) assertionVerification.getInput();
        if (response.getNumberOfRowsModified() == -1 &&
                DSFieldAssertionVerifier.CONTAINS_OPERATOR.equals(assertionProperties.getOperator())) {
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) response.getResultSet();

            for (Map<String, Object> row : resultSet) {
                if (assertionProperties.getValue().equals(row.get(assertionProperties.getField()))) {
                    result.setPassed(Boolean.TRUE);
                    break;
                }
            }
        }

        return result;
    }
}
