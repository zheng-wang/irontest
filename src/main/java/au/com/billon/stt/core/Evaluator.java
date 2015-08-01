package au.com.billon.stt.core;

import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;
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
    public EvaluationResponse evaluate(EvaluationRequest request) throws Exception {
        EvaluationResponse response = new EvaluationResponse();
        if (EvaluationRequest.EVALUATION_TYPE_XPATH.equals(request.getType())) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            InputSource inputSource = new InputSource(new StringReader(request.getInput()));
            InputSource inputSource2 = new InputSource(new StringReader(request.getInput()));
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
            System.out.println(request.getExpression());
            String stringResult = xpath.evaluate(request.getExpression(), inputSource2);
            System.out.println(stringResult);
            NodeList nodeList = (NodeList) xpath.evaluate(request.getExpression(), inputSource, XPathConstants.NODESET);
            System.out.println(nodeList);
            System.out.println(nodeList.getLength());
            String result = XMLUtils.domNodeListToString(nodeList);
            response.setResult(result);
        }
        return response;
    }

}
