package io.irontest.utils;

import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IronTestUtilsTest {
    @Test
    void prettyPrintJSONOrXML_NullInput() throws TransformerException, IOException {
        String input = null;
        String expectedOutput = null;
        assertEquals(expectedOutput, IronTestUtils.prettyPrintJSONOrXML(input));
    }

    /**
     * Only testing very simple JSON, as the method under test is mainly a 'router' to well tested JSON library.
     * @throws TransformerException
     * @throws IOException
     */
    @Test
    void prettyPrintJSONOrXML_ValidJSON() throws TransformerException, IOException {
        String input = "{\"a\":1}";
        String expectedOutput = "{" + System.lineSeparator() +
                "  \"a\" : 1" + System.lineSeparator() +
                "}";
        assertEquals(expectedOutput, IronTestUtils.prettyPrintJSONOrXML(input));
    }

    @Test
    void prettyPrintJSONOrXML_InvalidJSON() throws TransformerException, IOException {
        String input = "{\"a\":1";
        String expectedOutput = input;
        assertEquals(expectedOutput, IronTestUtils.prettyPrintJSONOrXML(input));
    }

    /**
     * Only testing very simple XML, as the method under test is mainly a 'router' to well tested XML library.
     * @throws TransformerException
     * @throws IOException
     */
    @Test
    void prettyPrintJSONOrXML_ValidXML() throws TransformerException, IOException {
        String input = "<root><a>1</a></root>";
        String expectedOutput = "<root>" + System.lineSeparator() +
                "  <a>1</a>" + System.lineSeparator() +
                "</root>" + System.lineSeparator();
        assertEquals(expectedOutput, IronTestUtils.prettyPrintJSONOrXML(input));
    }

    @Test
    void prettyPrintJSONOrXML_InvalidXML() throws TransformerException, IOException {
        String input = "<root>";
        String expectedOutput = input;
        assertEquals(expectedOutput, IronTestUtils.prettyPrintJSONOrXML(input));
    }
}
