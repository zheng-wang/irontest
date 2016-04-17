package io.irontest.handlers;

import io.irontest.models.Endpoint;
import io.irontest.utils.XMLUtils;
import org.reficio.ws.client.core.SoapClient;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPHandler implements IronTestHandler {
    public SOAPHandler() { }

    public String invoke(String request, Endpoint endpoint) throws Exception {
        SoapClient client = SoapClient.builder().endpointUri(endpoint.getUrl()).build();
        String response = client.post(request);
        return XMLUtils.prettyPrintXML(response);
    }
}
