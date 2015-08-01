package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class EvaluationResponse {
    private String result;
    private String errorMessage;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
