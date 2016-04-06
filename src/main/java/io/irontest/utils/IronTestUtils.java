package io.irontest.utils;

import io.irontest.models.*;

/**
 * Created by Zheng on 12/07/2015.
 */
public class IronTestUtils {

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

    public static Class getTeststepPropertiesClassByType(String teststepType) {
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststepType)) {
            return SOAPTeststepProperties.class;
        } else {
            return null;
        }
    }
}
