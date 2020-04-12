package io.irontest.views;

import io.irontest.utils.IronTestUtils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class IronTestUtilsFreeMarkerAdapter {
    public String prettyPrintJSONOrXML(String input) throws XPathExpressionException, TransformerException, IOException {
        return IronTestUtils.prettyPrintJSONOrXML(input);
    }

    public String base64EncodeByteArray(byte[] bytes) {
        return IronTestUtils.base64EncodeByteArray(bytes);
    }
}
