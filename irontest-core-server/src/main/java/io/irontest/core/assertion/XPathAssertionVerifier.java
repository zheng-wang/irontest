package io.irontest.core.assertion;

import com.sun.org.apache.xpath.internal.XPathException;
import io.irontest.core.IronTestNamespaceContext;
import io.irontest.models.NamespacePrefix;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XPathAssertionProperties;
import io.irontest.models.assertion.XPathAssertionVerificationResult;
import io.irontest.utils.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.List;

/**
 * Created by Zheng on 6/08/2015.
 */
public class XPathAssertionVerifier implements AssertionVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(XPathAssertionVerifier.class);

    /**
     *
     * @param assertion
     * @param input the XML String that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        XPathAssertionVerificationResult result = new XPathAssertionVerificationResult();
        XPathAssertionProperties otherProperties = (XPathAssertionProperties) assertion.getOtherProperties();

        //  validate other properties
        if (otherProperties == null || "".equals(StringUtils.trimToEmpty(otherProperties.getxPath()))) {
            result.setError("XPath not specified");
            result.setResult(TestResult.FAILED);
            return result;
        } else if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValue()))) {
            result.setError("Expected Value not specified");
            result.setResult(TestResult.FAILED);
            return result;
        }

        evaluateXPathExpression((String) input, otherProperties.getxPath(),
                otherProperties.getNamespacePrefixes(), result);
        result.setResult(result.getError() == null &&
                otherProperties.getExpectedValue().equals(result.getActualValue()) ?
                TestResult.PASSED : TestResult.FAILED);
        return result;
    }

    private void evaluateXPathExpression(String xmlInput, String xPathExpression,
                                         List<NamespacePrefix> namespacePrefixes,
                                         XPathAssertionVerificationResult result) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new IronTestNamespaceContext(namespacePrefixes));

        String actualValue = null;
        String errorMessage = null;
        try {
            InputSource inputSource = new InputSource(new StringReader(xmlInput));
            Object value = xpath.evaluate(xPathExpression, inputSource, XPathConstants.NODESET);
            actualValue = XMLUtils.domNodeListToString((NodeList) value);
        } catch (XPathExpressionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof XPathException &&
                    cause.getMessage().startsWith("Can not convert") && cause.getMessage().endsWith("!")) {
                //  The value is not of type NODESET. Swallow the exception and try STRING.
                InputSource inputSource2 = new InputSource(new StringReader(xmlInput));
                try {
                    actualValue = (String) xpath.evaluate(xPathExpression, inputSource2, XPathConstants.STRING);
                } catch (XPathExpressionException e1) {
                    LOGGER.error("Failed to verify XPath assertion.", e);
                    errorMessage = e.getMessage();
                }
            } else {
                LOGGER.error("Failed to verify XPath assertion.", e);
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to verify XPath assertion.", e);
            errorMessage = e.getMessage();
        }

        result.setError(errorMessage);
        result.setActualValue(actualValue);
    }
}
