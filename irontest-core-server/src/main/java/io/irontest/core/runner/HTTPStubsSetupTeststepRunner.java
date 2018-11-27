package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;

import java.util.Map;
import java.util.UUID;

public class HTTPStubsSetupTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) {
        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();

        //  reset mock server
        wireMockServer.resetAll();

        //  load stub mappings into mock server
        Map<Short, UUID> httpStubMappingInstanceIds = getTestcaseRunContext().getHttpStubMappingInstanceIds();
        HTTPStubsSetupTeststepProperties otherProperties = (HTTPStubsSetupTeststepProperties) teststep.getOtherProperties();
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stubMapping: otherProperties.getHttpStubMappings()) {
                //  create (clone) a new instance out of the stub spec, with UUID generated for the instance
                StubMapping stubInstance = StubMapping.buildFrom(StubMapping.buildJsonStringFor(stubMapping.getSpec()));
                httpStubMappingInstanceIds.put(stubMapping.getNumber(), stubInstance.getId());
                stubInstance.setDirty(false);
                stubMappings.addMapping(stubInstance);
            }
        });

        return new BasicTeststepRun();
    }
}
