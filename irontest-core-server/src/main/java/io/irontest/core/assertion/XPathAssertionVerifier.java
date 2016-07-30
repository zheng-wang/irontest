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
    public XPathAssertionVerifier() {}

    /**
     *
     * @param assertion
     * @param input the XML String that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Assertion assertion, Object input) {
        XPathAssertionVerificationResult result = new XPathAssertionVerificationResult();
        XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getOtherProperties();
        evaluateXPathExpression((String) input, assertionProperties.getxPath(),
                assertionProperties.getNamespacePrefixes(), result);
        result.setResult(result.getError() == null &&
                assertionProperties.getExpectedValue().equals(result.getActualValue()) ?
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
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }
            } else {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        result.setError(errorMessage);
        result.setActualValue(actualValue);
    }
}
