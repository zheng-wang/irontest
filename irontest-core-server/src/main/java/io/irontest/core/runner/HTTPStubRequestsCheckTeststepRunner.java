package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.irontest.models.teststep.Teststep;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HTTPStubRequestsCheckTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) {
        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();

        Map<UUID, Boolean> stubHitStatus = new HashMap<>();
        for (UUID uuid: getTestcaseRunContext().getHttpStubMappingInstanceIds().values()) {
            stubHitStatus.put(uuid, false);
        }

        for (ServeEvent serveEvent: wireMockServer.getAllServeEvents()) {
            if (serveEvent.getWasMatched() && stubHitStatus.keySet().contains(serveEvent.getStubMapping().getId())) {
                stubHitStatus.put(serveEvent.getStubMapping().getId(), true);
            }
        }

        return new BasicTeststepRun();
    }
}
