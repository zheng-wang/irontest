package io.irontest.core;

import io.irontest.models.*;

/**
 * Created by Zheng on 6/08/2015.
 */
public class XPathAssertionVerifier implements AssertionVerifier {
    public XPathAssertionVerifier() {}

    public Assertion verify(Assertion assertion) {
        AssertionVerification verification = assertion.getVerification();
        XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getProperties();
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                assertion.getType(), assertionProperties.getxPath(), verification.getInput(),
                new XPathEvaluationRequestProperties(assertionProperties.getNamespacePrefixes()));
        EvaluationResponse evaluationResponse = new EvaluatorFactory().createEvaluator(evaluationRequest).evaluate();
        verification.setPassed(evaluationResponse.getError() == null &&
                assertionProperties.getExpectedValue().equals(evaluationResponse.getActualValue()));
        verification.setError(evaluationResponse.getError());
        verification.setActualValue(evaluationResponse.getActualValue());
        return assertion;
    }
}
