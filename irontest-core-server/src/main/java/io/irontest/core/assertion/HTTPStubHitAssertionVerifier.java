package io.irontest.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;

import java.util.List;
import java.util.UUID;

public class HTTPStubHitAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object ...inputs) {
        AssertionVerificationResult result = new AssertionVerificationResult();

        List<ServeEvent> allServeEvents = (List<ServeEvent>) inputs[0];
        UUID stubInstanceUUID = (UUID) inputs[1];
        short stubInstanceHitCount = 0;
        for (ServeEvent serveEvent: allServeEvents) {
            if (serveEvent.getStubMapping().getId().equals(stubInstanceUUID)) {    //  the stub instance has been hit
                stubInstanceHitCount++;
            }
        }

        if (stubInstanceHitCount != 1) {
            result.setResult(TestResult.FAILED);
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
