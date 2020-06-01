package io.irontest.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used for passing information across test steps when running a test case.
 */
public class TestcaseRunContext {
    private Date testcaseRunStartTime;
    private Date testcaseIndividualRunStartTime;
    private WireMockServer wireMockServer;            //  the universal WireMock server inside the Iron Test instance
    private Map<Short, UUID> httpStubMappingInstanceIds = new HashMap<>();  //  mapping from stub mapping number to stub mapping instance UUID (after loaded into mock server)

    public Date getTestcaseRunStartTime() {
        return testcaseRunStartTime;
    }

    public void setTestcaseRunStartTime(Date testcaseRunStartTime) {
        this.testcaseRunStartTime = testcaseRunStartTime;
    }

    public Date getTestcaseIndividualRunStartTime() {
        return testcaseIndividualRunStartTime;
    }

    public void setTestcaseIndividualRunStartTime(Date testcaseIndividualRunStartTime) {
        this.testcaseIndividualRunStartTime = testcaseIndividualRunStartTime;
    }

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public void setWireMockServer(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    public Map<Short, UUID> getHttpStubMappingInstanceIds() {
        return httpStubMappingInstanceIds;
    }
}
