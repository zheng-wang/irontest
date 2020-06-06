package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.MessageEqualAssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.utils.XMLUtils;

public class XMLEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the XML string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object ...inputs) {
        XMLEqualAssertionProperties assertionProperties =
                (XMLEqualAssertionProperties) getAssertion().getOtherProperties();

        //  validate arguments
        if (assertionProperties.getExpectedXML() == null) {
            throw new IllegalArgumentException("Expected XML is null.");
        } else if (inputs[0] == null) {
            throw new IllegalArgumentException("Actual XML is null.");
        } else if (inputs[0].equals("")) {
            throw new IllegalArgumentException("Actual XML is empty.");
        }

        MessageEqualAssertionVerificationResult result = new MessageEqualAssertionVerificationResult();
        String differencesStr = XMLUtils.compareXML(assertionProperties.getExpectedXML(), (String) inputs[0]);
        if (differencesStr.length() > 0) {
            result.setResult(TestResult.FAILED);
            result.setDifferences(differencesStr);
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
