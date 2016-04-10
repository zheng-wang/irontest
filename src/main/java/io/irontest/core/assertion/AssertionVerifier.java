package io.irontest.core.assertion;

import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;

/**
 * Created by Zheng on 6/08/2015.
 */
public interface AssertionVerifier {
    AssertionVerificationResult verify(AssertionVerification assertionVerification);
}
