package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class EvaluationResponse {
    private String result;
    private boolean error;

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
