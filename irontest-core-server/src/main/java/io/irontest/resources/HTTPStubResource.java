package io.irontest.resources;

import io.irontest.db.HTTPStubMappingDAO;
import io.irontest.models.HTTPStubMapping;
import io.irontest.models.UserDefinedProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class HTTPStubResource {
    private HTTPStubMappingDAO httpStubMappingDAO;

    public HTTPStubResource(HTTPStubMappingDAO httpStubMappingDAO) {
        this.httpStubMappingDAO = httpStubMappingDAO;
    }

    @GET @Path("testcases/{testcaseId}/httpstubs")
    public List<HTTPStubMapping> findByTestcaseId(@PathParam("testcaseId") long testcaseId) {
        return httpStubMappingDAO.findByTestcaseId(testcaseId);
    }
}
