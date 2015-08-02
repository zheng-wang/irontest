package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;
import au.com.billon.stt.models.XPathEvaluationRequestProperties;
import au.com.billon.stt.utils.XMLUtils;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
            InputSource inputSource = new InputSource(new StringReader(request.getInput()));
            NodeList nodeList = null;
            String result = null;
            try {
                nodeList = (NodeList) xpath.evaluate(request.getExpression(), inputSource, XPathConstants.NODESET);
                result = XMLUtils.domNodeListToString(nodeList);
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                response.setError(true);
            }

            response.setResult(result);
        }
        return response;
    }

}
