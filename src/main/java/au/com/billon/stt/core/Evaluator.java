package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationRequest;
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
 * Created by Zheng on 27/07/2015.
 */
public class Evaluator {
    public EvaluationResponse evaluate(EvaluationRequest request) {
        EvaluationResponse response = new EvaluationResponse();
        if (EvaluationRequest.EVALUATION_TYPE_XPATH.equals(request.getType())) {
            XPathEvaluationRequestProperties properties = (XPathEvaluationRequestProperties) request.getProperties();
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new STTNamespaceContext(properties.getNamespacePrefixes()));

            String result = null;
            boolean error = false;
            try {
                InputSource inputSource = new InputSource(new StringReader(request.getInput()));
                Object value = xpath.evaluate(request.getExpression(), inputSource, XPathConstants.NODESET);
                result = XMLUtils.domNodeListToString((NodeList) value);
            } catch (XPathExpressionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof XPathException &&
                        cause.getMessage().startsWith("Can not convert") && cause.getMessage().endsWith("!")) {
                    //  The value is not of type NODESET. Swallow the exception and try STRING.
                    InputSource inputSource2 = new InputSource(new StringReader(request.getInput()));
                    try {
                        result = (String) xpath.evaluate(request.getExpression(), inputSource2, XPathConstants.STRING);
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
        }
        return response;
    }
}
