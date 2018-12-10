package io.irontest.resources;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/mockserver") @Produces({ MediaType.APPLICATION_JSON })
public class MockServerResource {
    private WireMockServer wireMockServer;

    public MockServerResource(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    @GET @Path("stubInstances")
    public List<StubMapping> findAllStubInstances() {
        return wireMockServer.getStubMappings();
    }
}
