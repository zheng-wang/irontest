package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class EvaluationResponse {
    //  message of error that occurred during the evaluation
    private String error;

    //  actual value of the expression evaluated against the input (see EvaluationRequest)
    private String actualValue;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }
}
