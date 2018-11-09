package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;

public class HTTPStubsSetupTeststepRunner extends TeststepRunner {
    @Override
    protected BasicTeststepRun run(Teststep teststep) {
        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();

        //  reset mock server
        wireMockServer.resetAll();

        //  load stub mappings into mock server
        HTTPStubsSetupTeststepProperties otherProperties = (HTTPStubsSetupTeststepProperties) teststep.getOtherProperties();
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stubMapping: otherProperties.getHttpStubMappings()) {
                StubMapping mapping = StubMapping.buildFrom(stubMapping.getSpecJson());
                mapping.setDirty(false);
                stubMappings.addMapping(mapping);
            }
        });

        return new BasicTeststepRun();
    }
}
