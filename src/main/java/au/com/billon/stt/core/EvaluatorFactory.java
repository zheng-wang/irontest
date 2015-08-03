package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.XPathEvaluationRequestProperties;

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

    public Evaluator createEvaluator(String intfaceType, String assertionType) {
        Evaluator result = null;

        if (intfaceType.equals("DBInterface")) {
            if (assertionType.equals("DSField")) {
                result = new DSFieldEvaluator();
            }
        }

        return result;
    }
}
