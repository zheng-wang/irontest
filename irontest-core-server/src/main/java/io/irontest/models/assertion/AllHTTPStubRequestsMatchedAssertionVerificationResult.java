package io.irontest.models.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import java.util.List;

public class AllHTTPStubRequestsMatchedAssertionVerificationResult extends AssertionVerificationResult {
    private List<ServeEvent> unmatchedStubRequests;

    public List<ServeEvent> getUnmatchedStubRequests() {
        return unmatchedStubRequests;
    }

    public void setUnmatchedStubRequests(List<ServeEvent> unmatchedStubRequests) {
        this.unmatchedStubRequests = unmatchedStubRequests;
    }
}
