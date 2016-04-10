package io.irontest.models.assertion;

/**
 * Input to assertion verifier.
 * Created by Zheng on 5/08/2015.
 */
public class AssertionVerification {
    private Assertion assertion;     //  the assertion to be verified (against the input)
    private Object input;            //  the object that the assertion is verified against

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public void setAssertion(Assertion assertion) { this.assertion = assertion; }

    public Assertion getAssertion() { return assertion; }
}
