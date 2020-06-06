package io.irontest.core.teststep;

import io.irontest.models.HTTPMethod;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.SOAPTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;

public class SOAPTeststepRunner extends TeststepRunner {
    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();
        SOAPTeststepProperties otherProperties = (SOAPTeststepProperties) teststep.getOtherProperties();
        HTTPAPIResponse apiResponse = IronTestUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                HTTPMethod.POST, otherProperties.getHttpHeaders(), (String) teststep.getRequest());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
