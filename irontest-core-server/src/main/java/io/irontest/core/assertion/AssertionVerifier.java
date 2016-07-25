package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;

/**
 * Created by Zheng on 6/08/2015.
 */
public interface AssertionVerifier {
    /**
     * This method does not throw any exception. Capture exception message in AssertionVerificationResult.error if
     * there is any unexpected exception during verification. This is to enable test case to run silently.
     * @param assertion the assertion to be verified (against the input)
     * @param input the object that the assertion is verified against
     * @return
     */
    AssertionVerificationResult verify(Assertion assertion, Object input);
}
