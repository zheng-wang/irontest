package au.com.billon.stt.resources;

import au.com.billon.stt.Utils;
import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.db.TeststepPropertyDAO;
import au.com.billon.stt.models.*;
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
    private final TeststepDAO stepDAO;
    private final TeststepPropertyDAO propertyDAO;

    public TeststepResource(TeststepDAO stepDAO, TeststepPropertyDAO propertyDAO) {
        this.stepDAO = stepDAO;
        this.propertyDAO = propertyDAO;
    }

    @POST
    public Teststep create(Teststep teststep) throws JsonProcessingException {
        long id = stepDAO.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point
        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public Teststep findById(@PathParam("teststepId") long teststepId) {
        return stepDAO.findById(teststepId);
    }

    @PUT @Path("{teststepId}")
    public SOAPTeststep update(SOAPTeststep teststep) {
        stepDAO.update(teststep);
        TeststepProperty soapAddressProperty = new TeststepProperty(
                teststep.getId(),
                TeststepProperty.PROPERTY_NAME_SOAP_ADDRESS,
                teststep.getSoapAddress());
        propertyDAO.updateByTeststepIdAndPropertyName(soapAddressProperty);
        //return findById(teststep.getId());
        return null;
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        stepDAO.deleteById(teststepId);
    }

    // This is not a REST service. It is actually an RPC through JSON.
    // It is implemented for simplicity for now.
    @POST @Path("{teststepId}/invoke")
    public SOAPInvocationResponse invoke(TeststepInvocation invocation) throws TransformerException {
        SoapClient client = SoapClient.builder().endpointUri(invocation.getSoapAddress()).build();
        String response = client.post(invocation.getRequest());
        SOAPInvocationResponse result = new SOAPInvocationResponse(Utils.prettyPrintXML(response));
        return result;
    }
}
