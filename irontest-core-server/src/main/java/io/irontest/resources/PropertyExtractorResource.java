package io.irontest.resources;

import io.irontest.db.PropertyExtractorDAO;
import io.irontest.models.teststep.PropertyExtractionRequest;
import io.irontest.models.teststep.PropertyExtractionResult;
import io.irontest.models.teststep.PropertyExtractor;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class PropertyExtractorResource {
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
    @POST @Path("propertyExtractors/{propertyExtractorId}/run")
    @PermitAll
    public PropertyExtractionResult run(PropertyExtractionRequest propertyExtractionRequest) {
        return null;
    }
}
