package au.com.billon.stt.core;

import au.com.billon.stt.models.DSFieldAssertionProperties;
import au.com.billon.stt.models.EvaluationResponse;
import au.com.billon.stt.models.Properties;

import java.util.List;
import java.util.Map;

/**
 * Created by adminuser on 8/3/15.
 */
public class DSFieldEvaluator implements Evaluator {
    public static final String CONTAINS_OPERATOR = "Contains";

    public EvaluationResponse evaluate() {
        return null;
    }

    public EvaluationResponse evaluate(Object response, Properties properties) {
        EvaluationResponse result = new EvaluationResponse();
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
