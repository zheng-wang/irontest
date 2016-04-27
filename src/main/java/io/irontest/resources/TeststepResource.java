package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.EndpointDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.models.Endpoint;
import io.irontest.models.Properties;
import io.irontest.models.SOAPTeststepProperties;
import io.irontest.models.Teststep;
import io.irontest.utils.WSDLParser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final TeststepDAO teststepDAO;
    private final EndpointDAO endpointDAO;

    public TeststepResource(TeststepDAO teststepDAO, EndpointDAO endpointDAO) {
        this.teststepDAO = teststepDAO;
        this.endpointDAO = endpointDAO;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        preCreationProcess(teststep);

        //  create test step
        long id = teststepDAO.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point

        return teststep;
    }

    //  adding more info to the teststep object
    private void preCreationProcess(Teststep teststep) {
        Properties properties = teststep.getProperties();

        //  create sample request
        String sampleRequest = null;
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            sampleRequest = WSDLParser.getSampleRequest((SOAPTeststepProperties) properties);
        } else if (Teststep.TEST_STEP_TYPE_DB.equals(teststep.getType())){
            sampleRequest = "select * from ? where ?";
        }
        teststep.setRequest(sampleRequest);

        //  create unmanaged endpoint
        Endpoint endpoint = new Endpoint();
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            endpoint.setType(Endpoint.ENDPOINT_TYPE_SOAP);
            endpoint.setUrl(WSDLParser.getAdhocAddress((SOAPTeststepProperties) properties));
        } else if (Teststep.TEST_STEP_TYPE_DB.equals(teststep.getType())) {
            endpoint.setType(Endpoint.TEST_STEP_TYPE_DB);
        }
        endpoint.setDescription("Unmanaged " + endpoint.getType() + " Endpoint");
        long endpointId = endpointDAO.insertUnmanagedEndpoint(endpoint);
        endpoint.setId(endpointId);
        teststep.setEndpoint(endpoint);
    }

    //  Find the teststep and its associated endpoint
    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        Teststep teststep = teststepDAO.findById(teststepId);
        Endpoint endpoint = endpointDAO.findById(teststep.getEndpoint().getId());
        teststep.setEndpoint(endpoint);
        return teststep;
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        Teststep oldTeststep = findById(teststep.getId());

        teststepDAO.update(teststep);
        if (teststep.getEndpoint().getEnvironmentId() == null) {    //  this is an unmanaged endpoint, so update it
            endpointDAO.update(teststep.getEndpoint());
        } else if (oldTeststep.getEndpoint().getEnvironmentId() == null) {
            //  delete the old unmanaged endpoint when a managed endpoint is associated with the test step
            endpointDAO.deleteById(oldTeststep.getEndpoint().getId());
        }

        return findById(teststep.getId());
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        Teststep teststep = findById(teststepId);
        teststepDAO.deleteById(teststepId);
        if (teststep.getEndpoint().getEnvironmentId() == null) {  //  delete the teststep's endpoint if it is unmanaged
            endpointDAO.deleteById(teststep.getEndpoint().getId());
        }
    }
}
