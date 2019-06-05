package io.irontest.resources;

import io.irontest.db.PropertyExtractorDAO;
import io.irontest.models.teststep.PropertyExtractionRequest;
import io.irontest.models.teststep.PropertyExtractionResult;
import io.irontest.models.teststep.PropertyExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class PropertyExtractorResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionResource.class);

    private PropertyExtractorDAO propertyExtractorDAO;
    public PropertyExtractorResource(PropertyExtractorDAO propertyExtractorDAO) {
        this.propertyExtractorDAO = propertyExtractorDAO;
    }

    @GET
    @Path("teststeps/{teststepId}/propertyExtractors")
    public List<PropertyExtractor> findByTeststepId(@PathParam("teststepId") long teststepId) {
        return propertyExtractorDAO.findByTeststepId(teststepId);
    }

    @POST
    @Path("teststeps/{teststepId}/propertyExtractors")
    @PermitAll
    public PropertyExtractor create(@PathParam("teststepId") long teststepId, PropertyExtractor propertyExtractor) {
        long id = propertyExtractorDAO.insert(teststepId, propertyExtractor);
        return propertyExtractorDAO.findById(id);
    }

    @PUT @Path("propertyExtractors/{propertyExtractorId}")
    @PermitAll
    public void update(PropertyExtractor propertyExtractor) {
        propertyExtractorDAO.update(propertyExtractor);
    }

    @DELETE @Path("propertyExtractors/{propertyExtractorId}")
    @PermitAll
    public void delete(@PathParam("propertyExtractorId") long propertyExtractorId) {
        propertyExtractorDAO.deleteById(propertyExtractorId);
    }

    /**
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param propertyExtractionRequest
     * @return
     */
    @POST @Path("propertyExtractors/{propertyExtractorId}/extract")
    @PermitAll
    public PropertyExtractionResult extract(PropertyExtractionRequest propertyExtractionRequest) {
        PropertyExtractor propertyExtractor = propertyExtractionRequest.getPropertyExtractor();
        String propertyExtractionInput = propertyExtractionRequest.getInput();
        PropertyExtractionResult result = new PropertyExtractionResult();
        try {
            result = propertyExtractor.extract(propertyExtractionInput);
        } catch (Exception e) {
            LOGGER.error("Failed to extract property", e);
            result.setError(e.getMessage());
        }
        return result;
    }
}
