package io.irontest.models.testrun;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.core.teststep.APIResponse;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.teststep.Teststep;
import io.irontest.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for test case running.
 */
public class TeststepRun extends TestRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private Teststep teststep;
    private APIResponse response;            //  API response (could be null when there is no response, no endpoint, no API invocation, or response is not used)
    private String infoMessage;         //  some additional information when the test step finishes running successfully
    private String errorMessage;        //  error message of running the test step (errors when verifying assertions are captured in AssertionVerification)
    private List<AssertionVerification> assertionVerifications = new ArrayList<>();

    public Teststep getTeststep() {
        return teststep;
    }

    public void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<AssertionVerification> getAssertionVerifications() {
        return assertionVerifications;
    }

    public void setAssertionVerifications(List<AssertionVerification> assertionVerifications) {
        this.assertionVerifications = assertionVerifications;
    }

    public APIResponse getResponse() {
        return response;
    }

    public void setResponse(APIResponse response) {
        this.response = response;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }
}
