package io.irontest.models.assertion;

public class HTTPStubHitAssertionVerificationResult extends AssertionVerificationResult {
    private short actualHitCount;

    public short getActualHitCount() {
        return actualHitCount;
    }

    public void setActualHitCount(short actualHitCount) {
        this.actualHitCount = actualHitCount;
    }
}
