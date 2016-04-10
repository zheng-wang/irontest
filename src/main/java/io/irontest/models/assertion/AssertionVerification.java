package io.irontest.models.assertion;

/**
 * Created by Zheng on 5/08/2015.
 */
public class AssertionVerification {
    private String input;            //  the string that the assertion is verified against
    private Boolean passed;          //  true if assertion verification passed, false otherwise, null if not verified
    private String error;            //  message of error occurred during verification

    //  actualValue is currently used for xpath assertion only.
    //  Might need to refactor to be Properties when new type of assertion needs to be verified.
    private String actualValue;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
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
