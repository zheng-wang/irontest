package io.irontest.core.assertion;

import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionVerificationResult;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(AssertionVerification assertionVerification) {
        XMLEqualAssertionVerificationResult result = new XMLEqualAssertionVerificationResult();
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties)
                assertionVerification.getAssertion().getOtherProperties();

        if (assertionVerification.getInput() == null) {
            result.setError("Actual XML is null.");
            result.setPassed(false);
        } else {
            try {
                Diff diff = DiffBuilder
                        .compare(assertionProperties.getExpectedXML())
                        .withTest(assertionVerification.getInput())
                        .normalizeWhitespace()
                        .build();
                if (diff.hasDifferences()) {
                    result.setPassed(false);
                    result.setDifferences(diff.toString());
                } else {
                    result.setPassed(true);
                }
            } catch (XMLUnitException e) {
                result.setError(e.getMessage());
                result.setPassed(false);
            }
        }

        return result;
    }
}
