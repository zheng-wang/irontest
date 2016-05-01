package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public TeststepResource(TeststepDAO teststepDAO) {
        this.teststepDAO = teststepDAO;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        preCreationProcess(teststep);

        teststepDAO.insert(teststep);
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
        endpoint.setName("Unmanaged Endpoint");
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            endpoint.setType(Endpoint.ENDPOINT_TYPE_SOAP);
            endpoint.setUrl(WSDLParser.getAdhocAddress((SOAPTeststepProperties) properties));
        } else if (Teststep.TEST_STEP_TYPE_DB.equals(teststep.getType())) {
            endpoint.setType(Endpoint.TEST_STEP_TYPE_DB);
        }
        teststep.setEndpoint(endpoint);
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        return teststepDAO.findById(teststepId);
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        return teststepDAO.update(teststep);
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        teststepDAO.deleteById(teststepId);
    }
}
