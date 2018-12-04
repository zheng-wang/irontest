package io.irontest.resources;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.db.HTTPStubMappingDAO;
import io.irontest.models.HTTPStubMapping;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class HTTPStubResource {
    private HTTPStubMappingDAO httpStubMappingDAO;

    public HTTPStubResource(HTTPStubMappingDAO httpStubMappingDAO) {
        this.httpStubMappingDAO = httpStubMappingDAO;
    }

    @GET @Path("testcases/{testcaseId}/httpstubs")
    @JsonView(ResourceJsonViews.HTTPStubUIGrid.class)
    public List<HTTPStubMapping> findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return httpStubMappingDAO.findByTestcaseId(testcaseId);
    }

    @POST
    @Path("testcases/{testcaseId}/httpstubs")
    @PermitAll
    public HTTPStubMapping create(@PathParam("testcaseId") long testcaseId) {
        return httpStubMappingDAO.insert(testcaseId);
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
}
