package io.irontest.core.assertion;

import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;

/**
 * Created by Zheng on 6/08/2015.
 */
public interface AssertionVerifier {
    /**
     * This method does not throw any exception. Capture exception message in AssertionVerificationResult.error if
     * there is any unexpected exception during verification. This is to enable test case to run silently.
     * @param assertionVerification
     * @return
     */
    AssertionVerificationResult verify(AssertionVerification assertionVerification);
}
