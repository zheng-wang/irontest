package io.irontest.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.HTTPStubsHitInOrderAssertionProperties;
import io.irontest.models.assertion.HTTPStubsHitInOrderAssertionVerificationResult;

import java.util.*;

import static io.irontest.IronTestConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_NUMBER;

public class HTTPStubsHitInOrderAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        HTTPStubsHitInOrderAssertionVerificationResult result = new HTTPStubsHitInOrderAssertionVerificationResult();
        HTTPStubsHitInOrderAssertionProperties otherProperties =
                (HTTPStubsHitInOrderAssertionProperties) getAssertion().getOtherProperties();

        Map<Date, Short> hitMap = new TreeMap<>();
        List<ServeEvent> allServeEvents = (List<ServeEvent>) inputs[0];
        for (ServeEvent serveEvent: allServeEvents) {
            if (serveEvent.getWasMatched()) {
                StubMapping stubMapping = serveEvent.getStubMapping();
                hitMap.put(serveEvent.getRequest().getLoggedDate(),
                        (Short) stubMapping.getMetadata().get(WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_NUMBER));
            }
        }

        List<Short> actualHitOrder = new ArrayList(hitMap.values());
        result.setResult(otherProperties.getExpectedHitOrder().equals(actualHitOrder) ? TestResult.PASSED : TestResult.FAILED);
        result.setActualHitOrder(actualHitOrder);

        return result;
    }
}
