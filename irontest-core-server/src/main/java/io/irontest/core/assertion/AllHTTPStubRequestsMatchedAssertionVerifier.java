package io.irontest.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AllHTTPStubRequestsMatchedAssertionVerificationResult;
import io.irontest.models.assertion.AssertionVerificationResult;

import java.util.ArrayList;
import java.util.List;

public class AllHTTPStubRequestsMatchedAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        AllHTTPStubRequestsMatchedAssertionVerificationResult result = new AllHTTPStubRequestsMatchedAssertionVerificationResult();

        List<ServeEvent> allStubRequests = (List<ServeEvent>) inputs[0];
        List<ServeEvent> unmatchedStubRequests = new ArrayList<>();
        for (ServeEvent serveEvent: allStubRequests) {
            if (!serveEvent.getWasMatched()) {
                unmatchedStubRequests.add(serveEvent);
            }
        }

        result.setUnmatchedStubRequests(unmatchedStubRequests);

        if (unmatchedStubRequests.isEmpty()) {
            result.setResult(TestResult.PASSED);
        } else {
            result.setResult(TestResult.FAILED);
        }

        return result;
    }
}
