package io.irontest.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

public final class XMLUtils {
    /**
     * If the input string is well formed XML, return its pretty-print format. Otherwise, return it as is.
     * If the input is null, return null.
     * @param input
     * @return
     * @throws TransformerException
     */
    public static String prettyPrintXML(String input) throws TransformerException {
        if (input == null) {
            return null;
        } else {
            Document doc = null;
            try {
                doc = XMLUtils.xmlStringToDOM(input);
            } catch (Exception e) {
                //  the input string is not well formed XML
                return input;
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            if (input.startsWith("<?xml")) {
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");  // add line break after xml declaration
            } else {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            return result.getWriter().toString();
        }
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
        } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            writer.write(node.getNodeValue());
        } else {  // this block of code will always transform text node or attribute node to empty string, so handle them separately
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        }

        return writer.toString();
    }

    public static Document xmlStringToDOM(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(null);  //  prevent XML parser logging
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    /**
     * @param control
     * @param test
     * @return differences found, in a format for print
     */
    public static String compareXML(String control, String test) {
        StringBuilder differencesSB = new StringBuilder();
        Diff diff;
        try {
            diff = DiffBuilder
                    .compare(control)
                    .withTest(test)
                    .normalizeWhitespace()
                    //  Use custom DifferenceEvaluator in combination with the default DifferenceEvaluator, to utilize
                    //  the default DifferenceEvaluator's feature which turns some DIFFERENT comparison results into
                    //  SIMILAR, such as for different namespace prefixes.
                    .withDifferenceEvaluator(DifferenceEvaluators.chain(
                            DifferenceEvaluators.Default, new PlaceholderDifferenceEvaluator("#\\{", null)))
                    .build();
        } catch (XMLUnitException e) {
            throw new RuntimeException(e.getCause().getMessage());
        }
        if (diff.hasDifferences()) {
            Iterator it = diff.getDifferences().iterator();
            while (it.hasNext()) {
                Difference difference = (Difference) it.next();
                if (difference.getResult() == ComparisonResult.DIFFERENT) {   //  ignore SIMILAR comparison results
                    if (differencesSB.length() > 0) {
                        differencesSB.append("\n");
                    }
                    differencesSB.append(difference.getComparison().toString());
                }
            }
        }
        return differencesSB.toString();
    }
}
