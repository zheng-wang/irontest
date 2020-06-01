package io.irontest.core.teststep;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.HTTPTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;

public class HTTPTeststepRunner extends TeststepRunner {
    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();
        HTTPTeststepProperties otherProperties = (HTTPTeststepProperties) teststep.getOtherProperties();
        HTTPAPIResponse apiResponse = IronTestUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                otherProperties.getHttpMethod(), otherProperties.getHttpHeaders(), (String) teststep.getRequest());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
