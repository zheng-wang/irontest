package io.irontest.core;

import io.irontest.models.assertion.Assertion;

/**
 * Created by Zheng on 6/08/2015.
 */
public interface AssertionVerifier {
    Assertion verify(Assertion assertion);
}
