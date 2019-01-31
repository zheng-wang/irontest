package io.irontest.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.HTTPStubsHitInOrderAssertionVerificationResult;

import java.util.List;

public class HTTPStubsHitInOrderAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object... inputs) {
        HTTPStubsHitInOrderAssertionVerificationResult result = new HTTPStubsHitInOrderAssertionVerificationResult();

        List<ServeEvent> allStubRequests = (List<ServeEvent>) inputs[0];

        return result;
    }
}
