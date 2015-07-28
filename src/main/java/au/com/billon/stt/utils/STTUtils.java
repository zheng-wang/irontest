package au.com.billon.stt.utils;

import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.ContainsAssertionProperties;
import au.com.billon.stt.models.DSFieldAssertionProperties;
import au.com.billon.stt.models.XPathAssertionProperties;

/**
 * Created by Zheng on 12/07/2015.
 */
public class STTUtils {

    public static Class getAssertionPropertiesClassByType(String assertionType) {
        if (Assertion.ASSERTION_TYPE_CONTAINS.equals(assertionType)) {
            return ContainsAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_XPATH.equals(assertionType)) {
            return XPathAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_DSFIELD.equals(assertionType)) {
            return DSFieldAssertionProperties.class;
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }
    }
}
