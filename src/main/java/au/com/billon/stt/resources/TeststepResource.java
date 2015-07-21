package au.com.billon.stt.resources;

import au.com.billon.stt.Utils;
import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.models.SOAPTeststepInvocationRequestProperties;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.models.TeststepInvocationRequest;
import au.com.billon.stt.models.TeststepInvocationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.reficio.ws.client.core.SoapClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.TransformerException;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/testcases/{testcaseId}/teststeps") @Produces({ MediaType.APPLICATION_JSON })
public class TeststepResource {
    private final TeststepDAO dao;

    public TeststepResource(TeststepDAO dao) {
        this.dao = dao;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        long id = dao.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point
        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        return dao.findById(teststepId);
    }

    @PUT @Path("{teststepId}")
    public Teststep update(Teststep teststep) throws JsonProcessingException {
        dao.update(teststep);
        return findById(teststep.getId());
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        dao.deleteById(teststepId);
    }

    // This is not a REST service. It is actually an RPC through JSON.
    // It is implemented for simplicity for now.
    @POST @Path("{teststepId}/invoke")
    public TeststepInvocationResponse invoke(TeststepInvocationRequest invocationRequest) throws TransformerException {
        TeststepInvocationResponse invocationResponse = null;

        if (TeststepInvocationRequest.TESTSTEP_INVOCATION_TYPE_SOAP.equals(invocationRequest.getType())) {
            SOAPTeststepInvocationRequestProperties properties =
                    (SOAPTeststepInvocationRequestProperties) invocationRequest.getProperties();
            SoapClient client = SoapClient.builder().endpointUri(properties.getSoapAddress()).build();
            String response = client.post(invocationRequest.getRequest());
            invocationResponse = new TeststepInvocationResponse(Utils.prettyPrintXML(response));
        }

        return invocationResponse;
    }
}
