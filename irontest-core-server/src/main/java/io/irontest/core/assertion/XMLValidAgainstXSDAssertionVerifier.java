package io.irontest.core.assertion;

import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.XMLValidAgainstXSDAssertionProperties;
import io.irontest.models.assertion.XMLValidAgainstXSDAssertionVerificationResult;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XMLValidAgainstXSDAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs  contains only one argument: the XML string that the assertion is verifying against the XSD
     * @return
     * @throws Exception
     */
    @Override
    public AssertionVerificationResult verify(Object... inputs) throws Exception {
        String xmlString = (String) inputs[0];
        XMLValidAgainstXSDAssertionProperties assertionProperties =
                (XMLValidAgainstXSDAssertionProperties) getAssertion().getOtherProperties();
        String fileName = StringUtils.trimToEmpty(assertionProperties.getFileName());

        //  validate arguments
        if (xmlString == null) {
            throw new IllegalArgumentException("XML is null.");
        } else if ("".equals(fileName)) {
            throw new IllegalArgumentException("XSD file not uploaded.");
        } else if (!(fileName.toLowerCase().endsWith(".xsd") || fileName.toLowerCase().endsWith(".zip"))) {
            throw new IllegalArgumentException("Unrecognized XSD file format.");
        }

        XMLValidAgainstXSDAssertionVerificationResult result = new XMLValidAgainstXSDAssertionVerificationResult();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        Path tempDir = null;
        if (fileName.toLowerCase().endsWith(".xsd")) {
            schema = factory.newSchema(
                new StreamSource(new ByteArrayInputStream(assertionProperties.getFileBytes())));

        } else {        //  the XSD(s) are in a zip file
            //  extract the zip file in a temp directory
            tempDir = Files.createTempDirectory("irontest");
            File zipFile = new File(tempDir.toFile(), assertionProperties.getFileName());
            FileUtils.writeByteArrayToFile(zipFile, assertionProperties.getFileBytes());
            new ZipFile(zipFile).extractAll(tempDir.toString());

            //  create schema object out of the extracted XSD files
            Collection<File> xsdFiles = FileUtils.listFiles(tempDir.toFile(), new String[] { "xsd", "XSD" }, true);
            List<Source> sourceList = new ArrayList<>();
            for (File xsdFile: xsdFiles) {
                sourceList.add(new StreamSource(xsdFile));
            }
            schema = factory.newSchema(sourceList.toArray(new Source[sourceList.size()]));
        }

        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(xmlString)));
            result.setResult(TestResult.PASSED);
        } catch (SAXParseException e) {
            result.setResult(TestResult.FAILED);
            result.setFailureDetails(e.toString());
        } finally {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir.toFile());
            }
        }

        return result;
    }
}
