package io.irontest.core;

import io.irontest.models.EvaluationResponse;
import io.irontest.models.Properties;

/**
 * Created by Zheng on 27/07/2015.
 */
public interface Evaluator {
    EvaluationResponse evaluate();
    EvaluationResponse evaluate(Object response, Properties properties);
}
