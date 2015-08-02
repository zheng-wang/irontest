package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationResponse;
import au.com.billon.stt.models.XPathEvaluationRequestProperties;
import au.com.billon.stt.utils.XMLUtils;
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

    public EvaluationResponse evaluate() {
        EvaluationResponse response = new EvaluationResponse();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new STTNamespaceContext(properties.getNamespacePrefixes()));

        String result = null;
        boolean error = false;
        try {
            InputSource inputSource = new InputSource(new StringReader(xmlInput));
            Object value = xpath.evaluate(xPathExpression, inputSource, XPathConstants.NODESET);
            result = XMLUtils.domNodeListToString((NodeList) value);
        } catch (XPathExpressionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof XPathException &&
                    cause.getMessage().startsWith("Can not convert") && cause.getMessage().endsWith("!")) {
                //  The value is not of type NODESET. Swallow the exception and try STRING.
                InputSource inputSource2 = new InputSource(new StringReader(xmlInput));
                try {
                    result = (String) xpath.evaluate(xPathExpression, inputSource2, XPathConstants.STRING);
                } catch (XPathExpressionException e1) {
                    e.printStackTrace();
                    result = e.getMessage();
                    error = true;
                }
            } else {
                e.printStackTrace();
                result = e.getMessage();
                error = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
            error = true;
        }

        response.setResult(result);
        response.setError(error);

        return response;
    }
}
