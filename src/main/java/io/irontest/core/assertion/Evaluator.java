package io.irontest.core.assertion;

import io.irontest.models.assertion.EvaluationResult;
import io.irontest.models.Properties;

/**
 * Created by Zheng on 27/07/2015.
 */
public interface Evaluator {
    EvaluationResult evaluate();
    EvaluationResult evaluate(Object response, Properties properties);
}
