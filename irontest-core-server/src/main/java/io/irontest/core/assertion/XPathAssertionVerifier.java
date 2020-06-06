package io.irontest.core.assertion;

import io.irontest.core.IronTestNamespaceContext;
import io.irontest.models.NamespacePrefix;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResultWithActualValue;
import io.irontest.models.assertion.XPathAssertionProperties;
import io.irontest.utils.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.List;

public class XPathAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the XML String that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResultWithActualValue verify(Object ...inputs) throws Exception {
        XPathAssertionProperties otherProperties = (XPathAssertionProperties) getAssertion().getOtherProperties();

        //  validate required parameters
        if ("".equals(StringUtils.trimToEmpty(otherProperties.getxPath()))) {
            throw new IllegalArgumentException("XPath not specified");
        } else if ("".equals(StringUtils.trimToEmpty(otherProperties.getExpectedValue()))) {
            throw new IllegalArgumentException("Expected Value not specified");
        } else if (inputs[0] == null) {
            throw new IllegalArgumentException("XML is null");
        }

        AssertionVerificationResultWithActualValue result = new AssertionVerificationResultWithActualValue();
        evaluateXPathExpression((String) inputs[0], otherProperties.getxPath(),
                otherProperties.getNamespacePrefixes(), result);
        result.setResult(otherProperties.getExpectedValue().equals(result.getActualValue()) ?
                TestResult.PASSED : TestResult.FAILED);
        return result;
    }

    private void evaluateXPathExpression(String xmlInput, String xPathExpression,
                                         List<NamespacePrefix> namespacePrefixes,
                                         AssertionVerificationResultWithActualValue result) throws TransformerException, XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new IronTestNamespaceContext(namespacePrefixes));

        InputSource inputSource = new InputSource(new StringReader(xmlInput));

        String actualValue;
        try {
            Object value = xpath.evaluate(xPathExpression, inputSource, XPathConstants.NODESET);
            actualValue = XMLUtils.domNodeListToString((NodeList) value);
        } catch (XPathExpressionException e) {
            if (e.getMessage().contains("Can not convert") && e.getMessage().endsWith("to a NodeList!")) {
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
