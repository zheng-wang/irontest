package au.com.billon.stt.handlers;

import au.com.billon.stt.Utils;
import org.reficio.ws.client.core.SoapClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPHandler implements STTHandler {
    public SOAPHandler() { }

    public String invoke(String request, Map<String, String> details) throws Exception {
        SoapClient client = SoapClient.builder().endpointUri(details.get("url")).build();
        String response = client.post(request);
        return Utils.prettyPrintXML(response);
    }

    public List<String> getProperties() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
