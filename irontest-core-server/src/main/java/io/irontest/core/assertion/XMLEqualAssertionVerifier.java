package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionVerificationResult;
import io.irontest.utils.XMLUtils;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerifier implements AssertionVerifier {
    /**
     *
     * @param assertion
     * @param input the XML String that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties == null || assertionProperties.getExpectedXML() == null) {
            throw new IllegalArgumentException("Expected XML is null.");
        } else if (input == null) {
            throw new IllegalArgumentException("Actual XML is null.");
        }

        XMLEqualAssertionVerificationResult result = new XMLEqualAssertionVerificationResult();
        StringBuilder differencesSB = XMLUtils.compareXML(assertionProperties.getExpectedXML(), (String) input);
        if (differencesSB.length() > 0) {
            result.setResult(TestResult.FAILED);
            result.setDifferences(differencesSB.toString());
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
