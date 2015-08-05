package au.com.billon.stt.models;

/**
 * Created by Zheng on 2/08/2015.
 */
public class AssertionVerificationRequest {
    private Assertion assertion;
    private String input;

    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
