package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationResponse;
import au.com.billon.stt.models.Properties;

/**
 * Created by Zheng on 27/07/2015.
 */
public interface Evaluator {
    EvaluationResponse evaluate();
    EvaluationResponse evaluate(Object response, Properties properties);
}
