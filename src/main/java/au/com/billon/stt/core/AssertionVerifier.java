package au.com.billon.stt.core;

import au.com.billon.stt.models.Assertion;

/**
 * Created by Zheng on 6/08/2015.
 */
public interface AssertionVerifier {
    Assertion verify(Assertion assertion);
}
