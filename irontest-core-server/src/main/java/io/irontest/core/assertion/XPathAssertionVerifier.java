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

import javax.xml.transform.TransformerException;
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
    public AssertionVerificationResult verify(Assertion assertion, Object input) throws Exception {
        XPathAssertionProperties otherProperties = (XPathAssertionProperties) assertion.getOtherProperties();

        //  validate other properties
        if (otherProperties == null || "".equals(StringUtils.trimToEmpty(otherProperties.getxPath()))) {
            throw new IllegalArgumentException("XPath not specified");
        } else if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValue()))) {
            throw new IllegalArgumentException("Expected Value not specified");
        }

        XPathAssertionVerificationResult result = new XPathAssertionVerificationResult();
        evaluateXPathExpression((String) input, otherProperties.getxPath(),
                otherProperties.getNamespacePrefixes(), result);
        result.setResult(otherProperties.getExpectedValue().equals(result.getActualValue()) ?
                TestResult.PASSED : TestResult.FAILED);
        return result;
    }

    private void evaluateXPathExpression(String xmlInput, String xPathExpression,
                                         List<NamespacePrefix> namespacePrefixes,
                                         XPathAssertionVerificationResult result) throws TransformerException, XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new IronTestNamespaceContext(namespacePrefixes));

        String actualValue = null;
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
                actualValue = (String) xpath.evaluate(xPathExpression, inputSource2, XPathConstants.STRING);
            } else {
                throw e;
            }
        }

        result.setActualValue(actualValue);
    }
}
