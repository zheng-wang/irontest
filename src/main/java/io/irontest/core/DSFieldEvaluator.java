package io.irontest.core;

import io.irontest.models.assertion.DSFieldAssertionProperties;
import io.irontest.models.assertion.EvaluationResult;
import io.irontest.models.Properties;

import java.util.List;
import java.util.Map;

/**
 * Created by adminuser on 8/3/15.
 */
public class DSFieldEvaluator implements Evaluator {
    public static final String CONTAINS_OPERATOR = "Contains";

    public EvaluationResult evaluate() {
        return null;
    }

    public EvaluationResult evaluate(Object response, Properties properties) {
        EvaluationResult result = new EvaluationResult();
        result.setError("true");

        DSFieldAssertionProperties assertionProperties = (DSFieldAssertionProperties) properties;

        if (DSFieldEvaluator.CONTAINS_OPERATOR.equals(assertionProperties.getOperator())) {
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) response;

            for (Map<String, Object> responseMap : responseList) {
                if (assertionProperties.getValue().equals(responseMap.get(assertionProperties.getField()))) {
                    result.setError("false");
                    break;
                }
            }
        }

        return result;
    }
}
