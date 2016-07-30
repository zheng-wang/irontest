package io.irontest.core.assertion;

import io.irontest.models.TestResult;
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
    /**
     *
     * @param assertion
     * @param input the XML String that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        XMLEqualAssertionVerificationResult result = new XMLEqualAssertionVerificationResult();
        XMLEqualAssertionProperties assertionProperties = (XMLEqualAssertionProperties) assertion.getOtherProperties();

        if (input == null) {
            result.setError("Actual XML is null.");
            result.setResult(TestResult.FAILED);
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
                        result.setResult(TestResult.FAILED);
                        result.setDifferences(differencesSB.toString());
                    } else {
                        result.setResult(TestResult.PASSED);
                    }
                } else {
                    result.setResult(TestResult.PASSED);
                }
            } catch (XMLUnitException e) {
                result.setError(e.getMessage());
                result.setResult(TestResult.FAILED);
            }
        }

        return result;
    }
}
