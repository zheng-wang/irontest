package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.models.assertion.MessageEqualAssertionVerificationResult;
import io.irontest.utils.XMLUtils;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param input the XML string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object input) throws Exception {
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties.getExpectedXML() == null) {
            throw new IllegalArgumentException("Expected XML is null.");
        } else if (input == null) {
            throw new IllegalArgumentException("Actual XML is null.");
        }

        MessageEqualAssertionVerificationResult result = new MessageEqualAssertionVerificationResult();
        String differencesStr = XMLUtils.compareXML(assertionProperties.getExpectedXML(), (String) input);
        if (differencesStr.length() > 0) {
            result.setResult(TestResult.FAILED);
            result.setDifferences(differencesStr);
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
