package io.irontest.core.runner;

import io.irontest.models.Teststep;
import io.irontest.utils.XMLUtils;
import org.reficio.ws.client.core.SoapClient;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPTeststepRunner implements TeststepRunner {
    public String run(Teststep teststep) throws Exception {
        SoapClient client = SoapClient.builder().endpointUri(teststep.getEndpoint().getUrl()).build();
        String response = client.post(teststep.getRequest());
        return XMLUtils.prettyPrintXML(response);
    }
}
