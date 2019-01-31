package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.irontest.core.MapValueLookup;
import io.irontest.db.DataTableDAO;
import io.irontest.db.HTTPStubMappingDAO;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.models.DataTable;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.UserDefinedProperty;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.text.StrSubstitutor;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static io.irontest.IronTestConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class HTTPStubResource {
    private HTTPStubMappingDAO httpStubMappingDAO;
    private WireMockServer wireMockServer;
    private UserDefinedPropertyDAO udpDAO;
    private DataTableDAO dataTableDAO;

    public HTTPStubResource(HTTPStubMappingDAO httpStubMappingDAO, WireMockServer wireMockServer,
                            UserDefinedPropertyDAO udpDAO, DataTableDAO dataTableDAO) {
        this.httpStubMappingDAO = httpStubMappingDAO;
        this.wireMockServer = wireMockServer;
        this.udpDAO = udpDAO;
        this.dataTableDAO = dataTableDAO;
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
    public void loadAll(@PathParam("testcaseId") long testcaseId) throws IOException {
        //  gather referenceable string properties
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findByTestcaseId(testcaseId);
        Map<String, String> referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        DataTable dataTable = dataTableDAO.getTestcaseDataTable(testcaseId, true);
        if (dataTable.getRows().size() > 0) {
            IronTestUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(referenceableStringProperties.keySet(), dataTable);
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(0));
        }

        List<HTTPStubMapping> stubs = httpStubMappingDAO.findByTestcaseId(testcaseId);

        //  resolve string property references in HTTPStubMapping objects
        List<String> undefinedStringProperties = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        IronTestUtils.addMixInsForWireMock(objectMapper);
        String httpStubMappingsJSON = objectMapper.writeValueAsString(stubs);
        MapValueLookup propertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);
        String resolvedHttpStubMappingsJSON = new StrSubstitutor(propertyReferenceResolver).replace(httpStubMappingsJSON);
        stubs = objectMapper.readValue(resolvedHttpStubMappingsJSON, new TypeReference<List<HTTPStubMapping>>() { });
        undefinedStringProperties.addAll(propertyReferenceResolver.getUnfoundKeys());
        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }

        IronTestUtils.substituteRequestBodyMainPatternValue(stubs);

        //  load stubs
        final List<HTTPStubMapping> finalStubs = stubs;
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stub: finalStubs) {
                //  delete old instances if exist
                List<StubMapping> existingInstances = wireMockServer.findStubMappingsByMetadata(
                        matchingJsonPath("$." + WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID,
                                equalTo(Long.toString(stub.getId()))));
                for (StubMapping existingInstance: existingInstances) {
                    wireMockServer.removeStubMapping(existingInstance);
                }

                StubMapping stubInstance = IronTestUtils.createStubInstance(stub.getId(), stub.getNumber(), stub.getSpec());
                stubMappings.addMapping(stubInstance);
            }
        });
    }

    @POST @Path("testcases/{testcaseId}/httpstubs/move")
    @PermitAll
    @JsonView(ResourceJsonViews.HTTPStubUIGrid.class)
    public List<HTTPStubMapping> move(@PathParam("testcaseId") long testcaseId,
                                          @QueryParam("fromNumber") short fromNumber, @QueryParam("toNumber") short toNumber) {
        httpStubMappingDAO.moveInTestcase(testcaseId, fromNumber, toNumber);
        return httpStubMappingDAO.findByTestcaseId(testcaseId);
    }
}
