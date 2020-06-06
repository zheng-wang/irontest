package io.irontest.core.teststep;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.utils.IronTestUtils;

import java.util.Map;
import java.util.UUID;

public class HTTPStubsSetupTeststepRunner extends TeststepRunner {
    @Override
    public BasicTeststepRun run() {
        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();

        //  reset mock server
        wireMockServer.resetAll();

        //  load stub mappings into mock server
        Map<Short, UUID> httpStubMappingInstanceIds = getTestcaseRunContext().getHttpStubMappingInstanceIds();
        HTTPStubsSetupTeststepProperties otherProperties = (HTTPStubsSetupTeststepProperties) getTeststep().getOtherProperties();
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stubMapping: otherProperties.getHttpStubMappings()) {
                StubMapping stubInstance = IronTestUtils.createStubInstance(stubMapping.getId(), stubMapping.getNumber(), stubMapping.getSpec());
                stubMappings.addMapping(stubInstance);
                httpStubMappingInstanceIds.put(stubMapping.getNumber(), stubInstance.getId());
            }
        });

        return new BasicTeststepRun();
    }
}
