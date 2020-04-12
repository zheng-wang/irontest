package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLValidAgainstXSDAssertionProperties;
import io.irontest.models.assertion.XMLValidAgainstXSDAssertionVerificationResult;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

public class XMLValidAgainstXSDAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param assertion
     * @param inputs  contains only one argument: the XML string that the assertion is verified against
     * @return
     * @throws Exception
     */
    @Override
    public AssertionVerificationResult _verify(Assertion assertion, Object... inputs) throws Exception {
        String xmlString = (String) inputs[0];
        XMLValidAgainstXSDAssertionProperties assertionProperties =
                (XMLValidAgainstXSDAssertionProperties) assertion.getOtherProperties();
        String fileName = assertionProperties.getFileName();

        //  validate arguments
        if (xmlString == null) {
            throw new IllegalArgumentException("XML is null.");
        } else if (fileName == null) {
            throw new IllegalArgumentException("XSD file not specified.");
        }

        XMLValidAgainstXSDAssertionVerificationResult result = new XMLValidAgainstXSDAssertionVerificationResult();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(
                new StreamSource(new ByteArrayInputStream(assertionProperties.getFileBytes())));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(xmlString)));
            result.setResult(TestResult.PASSED);
        } catch (SAXParseException e) {
            result.setResult(TestResult.FAILED);
            result.setFailureDetails(e.toString());
        }

        return result;
    }
}
