package au.com.billon.stt.parsers;

import au.com.billon.stt.models.SOAPTeststepProperties;
import au.com.billon.stt.models.TeststepProperties;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class SPDDBParser implements STTParser {
    public String getSampleRequest(TeststepProperties details) {
        return "select * from ? where ?";
    }

    public String getAdhocAddress(TeststepProperties details) {
        return null;
    }

    public List<String> getProperties() {
        String[] properties = {};
        return Arrays.asList(properties);
    }
}
