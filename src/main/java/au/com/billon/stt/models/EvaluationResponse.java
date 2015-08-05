package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class EvaluationResponse {
    //  whether any error occurs during the evaluation
    private boolean error;

    //  if error, then the result is the error message,
    //  otherwise the result is the actual value of the expression evaluated against the input (see EvaluationRequest)
    private String result;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
