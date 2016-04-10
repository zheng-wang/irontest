package io.irontest.core;

import io.irontest.models.EvaluationRequest;
import io.irontest.models.XPathEvaluationRequestProperties;

/**
 * Created by Zheng on 2/08/2015.
 */
public class EvaluatorFactory {
    public Evaluator createEvaluator(EvaluationRequest request) {
        Evaluator result = null;
        if (EvaluationRequest.EVALUATION_TYPE_XPATH.equals(request.getType())) {
            result = new XPathEvaluator(request.getInput(), request.getExpression(),
                    (XPathEvaluationRequestProperties) request.getProperties());
        } else {
            throw new RuntimeException("Unrecognized evaluation request type " + request.getType());
        }
        return result;
    }

    public Evaluator createEvaluator(String assertionType) {
        Evaluator result = null;
        if (assertionType.equals("DSField")) {
            result = new DSFieldEvaluator();
        }

        return result;
    }
}
