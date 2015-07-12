package au.com.billon.stt.resources;

import au.com.billon.stt.Utils;
import au.com.billon.stt.db.TeststepDAO;
import au.com.billon.stt.db.TeststepPropertyDAO;
import au.com.billon.stt.models.TeststepInvocation;
import au.com.billon.stt.models.SOAPTeststep;
import au.com.billon.stt.models.Teststep;
import au.com.billon.stt.models.TeststepProperty;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
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
    public Teststep create(SOAPTeststep teststep) {
        //  create sample soap request
        Wsdl wsdl = Wsdl.parse(teststep.getWsdlUrl());
        SoapBuilder builder = wsdl.binding().localPart(teststep.getWsdlBindingName()).find();
        SoapOperation operation = builder.operation().name(teststep.getWsdlOperationName()).find();
        teststep.setRequest(builder.buildInputMessage(operation));

        //  create test step
        long id = stepDAO.insert(teststep);
        teststep.setId(id);
        teststep.setRequest(null);  //  no need to bring request to client at this point

        //  create test step properties
        TeststepProperty property = new TeststepProperty(teststep.getId(),
                TeststepProperty.PROPERTY_NAME_SOAP_ADDRESS, builder.getServiceUrls().get(0));
        propertyDAO.insert(property);

        return teststep;
    }

    @GET
    @Path("{teststepId}")
    public SOAPTeststep findById(@PathParam("teststepId") long teststepId) {
        SOAPTeststep result = new SOAPTeststep(stepDAO.findById(teststepId));

        result.setSoapAddress(propertyDAO.findByTeststepIdAndPropertyName(
                teststepId,
                TeststepProperty.PROPERTY_NAME_SOAP_ADDRESS).getValue());
        return result;
    }

    @PUT @Path("{teststepId}")
    public SOAPTeststep update(SOAPTeststep teststep) {
        stepDAO.update(teststep);
        TeststepProperty soapAddressProperty = new TeststepProperty(
                teststep.getId(),
                TeststepProperty.PROPERTY_NAME_SOAP_ADDRESS,
                teststep.getSoapAddress());
        propertyDAO.updateByTeststepIdAndPropertyName(soapAddressProperty);
        return findById(teststep.getId());
    }

    @DELETE @Path("{teststepId}")
    public void delete(@PathParam("teststepId") long teststepId) {
        stepDAO.deleteById(teststepId);
    }

    // This is not a REST service. It is actually an RPC through JSON.
    // It is implemented for simplicity for now.
    @POST @Path("{teststepId}/invoke")
    public String invoke(TeststepInvocation invocation) throws TransformerException {
        SoapClient client = SoapClient.builder().endpointUri(invocation.getSoapAddress()).build();
        String response = client.post(invocation.getRequest());
        return Utils.prettyPrintXML(response);
    }
}
