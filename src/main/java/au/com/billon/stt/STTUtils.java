package au.com.billon.stt;

import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.ContainsAssertionProperties;
import au.com.billon.stt.models.DSFieldAssertionProperties;
import au.com.billon.stt.models.XPathAssertionProperties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Zheng on 12/07/2015.
 */
public class STTUtils {
    public static String prettyPrintXML(String xml) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        StreamSource source = new StreamSource(new StringReader(xml));
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public static Class getAssertionPropertiesClassByType(String assertionType) {
        if (Assertion.ASSERTION_TYPE_CONTAINS.equals(assertionType)) {
            return ContainsAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_XPATH.equals(assertionType)) {
            return XPathAssertionProperties.class;
        } else if (Assertion.ASSERTION_TYPE_DSFIELD.equals(assertionType)) {
            return DSFieldAssertionProperties.class;
        } else {
            throw new RuntimeException("Unrecognized assertion type " + assertionType);
        }
    }
}
