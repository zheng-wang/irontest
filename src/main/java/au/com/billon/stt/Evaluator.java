package au.com.billon.stt;

import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Zheng on 27/07/2015.
 */
public class Evaluator {
    public EvaluationResponse evaluate(EvaluationRequest request) throws Exception {
        EvaluationResponse response = new EvaluationResponse();
        if (EvaluationRequest.EVALUATION_TYPE_XPATH.equals(request.getType())) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            InputSource inputSource = new InputSource(new StringReader(request.getInput()));
            NodeList nodeList = (NodeList) xpath.evaluate(request.getExpression(), inputSource, XPathConstants.NODESET);
            String result = domNodeListToString(nodeList);
            response.setResult(result);
        }
        return response;
    }

    public String domNodeListToString(NodeList nodeList) throws TransformerException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            sb.append(domNodeToString(node)).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public String domNodeToString(Node node) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }
}
