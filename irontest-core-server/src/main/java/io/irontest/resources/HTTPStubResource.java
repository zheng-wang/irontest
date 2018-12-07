package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.db.HTTPStubMappingDAO;
import io.irontest.models.HTTPStubMapping;
import io.irontest.utils.IronTestUtils;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static io.irontest.IronTestConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class HTTPStubResource {
    private HTTPStubMappingDAO httpStubMappingDAO;
    private WireMockServer wireMockServer;

    public HTTPStubResource(HTTPStubMappingDAO httpStubMappingDAO, WireMockServer wireMockServer) {
        this.httpStubMappingDAO = httpStubMappingDAO;
        this.wireMockServer = wireMockServer;
    }

    @GET @Path("testcases/{testcaseId}/httpstubs")
    @JsonView(ResourceJsonViews.HTTPStubUIGrid.class)
    public List<HTTPStubMapping> findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return httpStubMappingDAO.findByTestcaseId(testcaseId);
    }

    @POST @Path("testcases/{testcaseId}/httpstubs")
    @PermitAll
    public HTTPStubMapping create(@PathParam("testcaseId") long testcaseId) {
        long id = httpStubMappingDAO.insert(testcaseId);
        return httpStubMappingDAO.findById(id);
    }

    @DELETE @Path("httpstubs/{httpStubId}")
    @PermitAll
    public void delete(@PathParam("httpStubId") long httpStubId) {
        httpStubMappingDAO.deleteById(httpStubId);
    }

    @GET @Path("testcases/{testcaseId}/httpstubs/{httpStubId}")
    public HTTPStubMapping findById(@PathParam("httpStubId") long httpStubId) {
        return httpStubMappingDAO.findById(httpStubId);
    }

    @PUT @Path("httpstubs/{httpStubId}")
    @PermitAll
    public void update(HTTPStubMapping stub) {
        httpStubMappingDAO.update(stub);
    }

    @POST @Path("testcases/{testcaseId}/httpstubs/loadAll")
    @PermitAll
    public void loadAll(@PathParam("testcaseId") long testcaseId) {
        List<HTTPStubMapping> stubs = httpStubMappingDAO.findByTestcaseId(testcaseId);
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stub: stubs) {
                //  delete old instances if exist
                List<StubMapping> existingInstances = wireMockServer.findStubMappingsByMetadata(
                        matchingJsonPath("$." + WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID,
                                equalTo(Long.toString(stub.getId()))));
                for (StubMapping existingInstance: existingInstances) {
                    wireMockServer.removeStubMapping(existingInstance);
                }

                StubMapping stubInstance = IronTestUtils.createStubInstance(stub.getId(), stub.getSpec());
                stubMappings.addMapping(stubInstance);
            }
        });
    }
}
