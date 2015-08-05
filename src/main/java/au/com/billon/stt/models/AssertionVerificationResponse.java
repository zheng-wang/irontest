package au.com.billon.stt.models;

/**
 * Created by Zheng on 2/08/2015.
 */
public class AssertionVerificationResponse {
    private boolean passed;

    //  message of error occurred during verification
    private String error;

    //  actualValue is currently used for xpath assertion only.
    //  Might need to refactor to be Properties when new type of assertion needs to be verified.
    private String actualValue;

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

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
