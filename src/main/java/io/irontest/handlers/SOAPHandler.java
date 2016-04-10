package io.irontest.handlers;

import io.irontest.models.SOAPTeststepProperties;
import io.irontest.utils.XMLUtils;
import org.reficio.ws.client.core.SoapClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPHandler implements IronTestHandler {
    public SOAPHandler() { }

    //  invoke the service without endpoint
    public String invoke(String request, SOAPTeststepProperties properties) throws Exception {
        SoapClient client = SoapClient.builder().endpointUri(properties.getSoapAddress()).build();
        String response = client.post(request);
        return XMLUtils.prettyPrintXML(response);
    }

    //  invoke the service using endpoint
    public String invoke(String request, Map<String, String> details) throws Exception {
        SoapClient client = SoapClient.builder().endpointUri(details.get("url")).build();
        String response = client.post(request);
        return XMLUtils.prettyPrintXML(response);
    }

    //  actually endpoint properties
    public List<String> getProperties() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
