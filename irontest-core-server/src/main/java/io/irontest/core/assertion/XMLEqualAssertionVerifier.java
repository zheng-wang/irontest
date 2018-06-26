package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.MessageEqualAssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.utils.XMLUtils;

public class XMLEqualAssertionVerifier extends AssertionVerifier {
    private static final String XML_UNIT_PLACEHOLDER_REGEX = "#\\{[\\s]*(xmlunit\\.[^}]+)}";
    private static final String XML_UNIT_PLACEHOLDER_DELIMITER_REPLACEMENT = "\\${$1}";

    /**
     *
     * @param assertion
     * @param input the XML string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object input) {
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties) assertion.getOtherProperties();

        //  validate arguments
        if (assertionProperties.getExpectedXML() == null) {
            throw new IllegalArgumentException("Expected XML is null.");
        } else if (input == null) {
            throw new IllegalArgumentException("Actual XML is null.");
        }

        String expectedXML = assertionProperties.getExpectedXML().replaceAll(
                XML_UNIT_PLACEHOLDER_REGEX, XML_UNIT_PLACEHOLDER_DELIMITER_REPLACEMENT);
        MessageEqualAssertionVerificationResult result = new MessageEqualAssertionVerificationResult();
        String differencesStr = XMLUtils.compareXML(expectedXML, (String) input);
        if (differencesStr.length() > 0) {
            result.setResult(TestResult.FAILED);
            result.setDifferences(differencesStr);
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
