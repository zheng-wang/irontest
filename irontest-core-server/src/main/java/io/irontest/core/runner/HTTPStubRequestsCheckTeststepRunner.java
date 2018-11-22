package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.irontest.models.teststep.Teststep;

public class HTTPStubRequestsCheckTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();

        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();
        WireMockServerAPIResponse response = new WireMockServerAPIResponse();

        response.setAllServeEvents(wireMockServer.getAllServeEvents());
        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }
}
