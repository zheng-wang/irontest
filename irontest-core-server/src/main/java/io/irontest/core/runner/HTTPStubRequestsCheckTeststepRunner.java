package io.irontest.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.models.teststep.Teststep;
import io.irontest.utils.IronTestUtils;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class HTTPStubRequestsCheckTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) throws IOException, TransformerException {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();

        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();
        WireMockServerAPIResponse response = new WireMockServerAPIResponse();
        String allServeEventsJSON =  new ObjectMapper().writeValueAsString(wireMockServer.getAllServeEvents());
        response.setAllServeEvents(IronTestUtils.prettyPrintJSONOrXML(allServeEventsJSON));
        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }
}
