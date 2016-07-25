package io.irontest.core.assertion;

import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLEqualAssertionProperties;
import io.irontest.models.assertion.XMLEqualAssertionVerificationResult;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import java.util.Iterator;

/**
 * Created by Zheng on 4/06/2016.
 */
public class XMLEqualAssertionVerifier implements AssertionVerifier {
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        XMLEqualAssertionVerificationResult result = new XMLEqualAssertionVerificationResult();
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties) assertion.getOtherProperties();

        if (input == null) {
            result.setError("Actual XML is null.");
            result.setPassed(false);
        } else {
            try {
                Diff diff = DiffBuilder
                        .compare(assertionProperties.getExpectedXML())
                        .withTest(input)
                        .normalizeWhitespace()
                        .build();
                if (diff.hasDifferences()) {
                    StringBuilder differencesSB = new StringBuilder();
                    Iterator it = diff.getDifferences().iterator();
                    while (it.hasNext()) {
                        Difference difference = (Difference) it.next();
                        if (difference.getResult() == ComparisonResult.DIFFERENT) {   //  ignore SIMILAR differences
                            if (differencesSB.length() > 0) {
                                differencesSB.append("\n");
                            }
                            differencesSB.append(difference.getComparison().toString());
                        }
                    }
                    if (differencesSB.length() > 0) {
                        result.setPassed(false);
                        result.setDifferences(differencesSB.toString());
                    } else {
                        result.setPassed(true);
                    }
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
