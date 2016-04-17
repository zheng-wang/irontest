package io.irontest.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.irontest.db.EndpointDAO;
import io.irontest.db.TeststepDAO;
import io.irontest.models.Endpoint;
import io.irontest.models.Properties;
import io.irontest.models.SOAPTeststepProperties;
import io.irontest.models.Teststep;
import io.irontest.parsers.ParserFactory;

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
        Properties properties = teststep.getProperties();

        String parserName = "DBInterface";
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            parserName = "WSDL";
        }

        String sampleRequest = ParserFactory.getInstance().getParser(parserName).getSampleRequest(properties);
        teststep.setRequest(sampleRequest);

        long id = teststepDAO.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point

        //  create unmanaged endpoint
        Endpoint endpoint = new Endpoint();
        endpoint.setTeststepId(new Long(id));
        endpoint.setDescription("Unmanaged Endpoint");
        if (Teststep.TEST_STEP_TYPE_SOAP.equals(teststep.getType())) {
            String adhocAddress = ParserFactory.getInstance().getParser(parserName).getAdhocAddress(properties);
            endpoint.setType(Endpoint.ENDPOINT_TYPE_SOAP);
            endpoint.setUrl(adhocAddress);
        }
        endpointDAO.insertUnmanagedEndpoint(endpoint);

        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        Teststep teststep = teststepDAO.findById(teststepId);
        Endpoint endpoint = endpointDAO.findByTeststepId(teststepId);
        teststep.setEndpoint(endpoint);
        return teststep;
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        teststepDAO.update(teststep);
        return findById(teststep.getId());
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        teststepDAO.deleteById(teststepId);
    }
}
