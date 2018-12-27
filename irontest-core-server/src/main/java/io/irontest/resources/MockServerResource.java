package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.utils.IronTestUtils;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/mockserver") @Produces({ MediaType.APPLICATION_JSON })
public class MockServerResource {
    private WireMockServer wireMockServer;

    public MockServerResource(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    @GET @Path("stubInstances")
    @JsonView(ResourceJsonViews.MockServerStubInstanceList.class)
    public List<StubMapping> findAllStubInstances() {
        return wireMockServer.getStubMappings();
    }

    @GET @Path("unmatchedStubRequests")
    @JsonView(ResourceJsonViews.MockServerUnmatchedRequestList.class)
    public List<ServeEvent> findAllUnmatchedStubRequests() {
        List<ServeEvent> result = new ArrayList<>();
        List<ServeEvent> serveEvents = wireMockServer.getAllServeEvents();
        for (ServeEvent serveEvent: serveEvents) {
            if (!serveEvent.getWasMatched()) {
                result.add(serveEvent);
            }
        }
        return result;
    }

    @GET @Path("stubInstances/{stubInstanceId}")
    public StubMapping findStubInstanceById(@PathParam("stubInstanceId") UUID stubInstanceId) {
        List<StubMapping> stubInstances = wireMockServer.getStubMappings();
        for (StubMapping stubInstance: stubInstances) {
            if (stubInstance.getId().equals(stubInstanceId)) {
                return stubInstance;
            }
        }
        return null;
    }

    @GET @Path("stubInstances/{stubInstanceId}/stubRequests")
    @JsonView(ResourceJsonViews.MockServerStubRequestList.class)
    public List<ServeEvent> findMatchedRequestsForStubInstance(@PathParam("stubInstanceId") UUID stubInstanceId) {
        List<ServeEvent> result = new ArrayList<>();
        List<ServeEvent> serveEvents = wireMockServer.getAllServeEvents();
        for (ServeEvent serveEvent: serveEvents) {
            if (serveEvent.getStubMapping().getId().equals(stubInstanceId)) {
                result.add(serveEvent);
            }
        }
        return result;
    }

    @GET @Path("stubRequests/{stubRequestId}")
    public ServeEvent findStubRequestById(@PathParam("stubRequestId") UUID stubRequestId) {
        List<ServeEvent> serveEvents = wireMockServer.getAllServeEvents();
        for (ServeEvent serveEvent: serveEvents) {
            if (serveEvent.getId().equals(stubRequestId)) {
                if (serveEvent.getWasMatched()) {
                    return serveEvent;
                } else {
                    return IronTestUtils.updateUnmatchedStubRequest(serveEvent, wireMockServer);
                }
            }
        }
        return null;
    }

    @POST @Path("clearRequestLog")
    @PermitAll
    public void clearRequestLog() {
        wireMockServer.resetRequests();
    }
}
