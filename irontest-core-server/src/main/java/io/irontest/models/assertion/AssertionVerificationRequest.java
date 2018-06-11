package io.irontest.models.assertion;

public class AssertionVerificationRequest {
    private Assertion assertion;
    private Object input;

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public void setAssertion(Assertion assertion) { this.assertion = assertion; }

    public Assertion getAssertion() { return assertion; }
}
