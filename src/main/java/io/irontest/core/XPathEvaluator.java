package io.irontest.core;

import io.irontest.models.EvaluationResult;
import io.irontest.models.Properties;
import io.irontest.models.XPathEvaluationRequestProperties;
import io.irontest.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.XPathException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Created by Zheng on 2/08/2015.
 */
public class XPathEvaluator implements Evaluator {
    private String xmlInput;
    private String xPathExpression;
    private XPathEvaluationRequestProperties properties;

    public XPathEvaluator(String xmlInput, String xPathExpression, XPathEvaluationRequestProperties properties) {
        this.xmlInput = xmlInput;
        this.xPathExpression = xPathExpression;
        this.properties = properties;
    }

    public EvaluationResult evaluate(Object response, Properties properties) {
        return null;
    }

    public EvaluationResult evaluate() {
        EvaluationResult response = new EvaluationResult();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new IronTestNamespaceContext(properties.getNamespacePrefixes()));

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

        response.setError(errorMessage);
        response.setActualValue(actualValue);

        return response;
    }
}
