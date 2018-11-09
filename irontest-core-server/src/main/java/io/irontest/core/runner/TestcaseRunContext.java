package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.Date;

/**
 * Used for passing information across test steps when running a test case.
 */
public class TestcaseRunContext {
    private Date testcaseRunStartTime;
    private Date testcaseIndividualRunStartTime;
    private WireMockServer wireMockServer;            //  the universal WireMock server inside the Iron Test instance

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
}
