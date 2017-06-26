package io.irontest.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created by Zheng on 28/07/2015.
 */
public class XMLUtils {
    public static String prettyPrintXML(String xml) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        StreamSource source = new StreamSource(new StringReader(xml));
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public static String domNodeListToString(NodeList nodeList) throws TransformerException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            sb.append(domNodeToString(node));
            // Below formatting will cause xpath assertion verification failure when
            // xpath evaluation result is of type NODESET.
            //sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public static String domNodeToString(Node node) throws TransformerException {
        StringWriter writer = new StringWriter();
        if (node.getNodeType() == Node.TEXT_NODE) {
            writer.write(node.getTextContent());
        } else {  // this block of code will always transform text node to empty string, so handle text node separately
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        }

        return writer.toString();
    }

    public static Document xmlStringToDOM(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    /**
     * @param control
     * @param test
     * @return differences found
     */
    public static StringBuilder compareXML(String control, String test) {
        StringBuilder differencesSB = new StringBuilder();
        Diff diff = DiffBuilder
                .compare(control)
                .withTest(test)
                .normalizeWhitespace()
                .build();
        if (diff.hasDifferences()) {
            Iterator it = diff.getDifferences().iterator();
            while (it.hasNext()) {
                Difference difference = (Difference) it.next();
                if (difference.getResult() == ComparisonResult.DIFFERENT) {   //  ignore SIMILAR differences
                    if (differencesSB.length() > 0) {
                        differencesSB.append("\n");
                    }
                    differencesSB.append(difference.getComparison().toString());
                }
            }
        }
        return differencesSB;
    }
}
