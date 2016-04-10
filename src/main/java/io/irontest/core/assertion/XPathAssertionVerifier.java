package io.irontest.core.assertion;

import io.irontest.models.assertion.*;

/**
 * Created by Zheng on 6/08/2015.
 */
public class XPathAssertionVerifier implements AssertionVerifier {
    public XPathAssertionVerifier() {}

    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        Assertion assertion = assertionVerification.getAssertion();
        XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getProperties();
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                assertion.getType(), assertionProperties.getxPath(), (String) assertionVerification.getInput(),
                new XPathEvaluationRequestProperties(assertionProperties.getNamespacePrefixes()));
        EvaluationResult evaluationResult = new EvaluatorFactory().createEvaluator(evaluationRequest).evaluate();
        result.setPassed(evaluationResult.getError() == null &&
                assertionProperties.getExpectedValue().equals(evaluationResult.getActualValue()));
        result.setError(evaluationResult.getError());
        result.setActualValue(evaluationResult.getActualValue());
        return result;
    }
}
