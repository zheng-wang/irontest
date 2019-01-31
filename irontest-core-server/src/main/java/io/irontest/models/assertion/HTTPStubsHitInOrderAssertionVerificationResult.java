package io.irontest.models.assertion;

import java.util.List;

public class HTTPStubsHitInOrderAssertionVerificationResult extends AssertionVerificationResult {
    private List<Short> actualHitOrder;

    public List<Short> getActualHitOrder() {
        return actualHitOrder;
    }

    public void setActualHitOrder(List<Short> actualHitOrder) {
        this.actualHitOrder = actualHitOrder;
    }
}
