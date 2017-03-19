package io.irontest.resources;

import io.irontest.models.WSDLBinding;
import io.irontest.models.teststep.SOAPOperationInfo;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/wsdls") @Produces({ MediaType.APPLICATION_JSON })
public class WSDLResource {
    public WSDLResource() {}

    @GET @Path("/{wsdlUrl}/bindings")
    public List<WSDLBinding> getWSDLBindings(@PathParam("wsdlUrl") String wsdlUrl) throws UnsupportedEncodingException {
        List<WSDLBinding> result = new ArrayList<WSDLBinding>();
        Wsdl wsdl = Wsdl.parse(wsdlUrl);
        List<QName> bindings = wsdl.getBindings();
        for (QName binding: bindings) {
            SoapBuilder builder = wsdl.getBuilder(binding);
            List<SoapOperation> operations = builder.getOperations();
            List<String> operationNames = new ArrayList<String>();
            for (SoapOperation operation: operations) {
                operationNames.add(operation.getOperationName());
            }
            result.add(new WSDLBinding(binding.getLocalPart(), operationNames));
        }

        return result;
    }

    @GET @Path("/{wsdlUrl}/bindings/{bindingName}/operations/{operationName}")
    public SOAPOperationInfo getOperationInfo(@PathParam("wsdlUrl") String wsdlUrl, @PathParam("bindingName") String bindingName,
                                              @PathParam("operationName") String operationName) {
        SOAPOperationInfo info = new SOAPOperationInfo();
        Wsdl wsdl = Wsdl.parse(wsdlUrl);
        SoapBuilder builder = wsdl.binding().localPart(bindingName).find();
        SoapOperation operation = builder.operation().name(operationName).find();
        info.setSampleRequest(builder.buildInputMessage(operation));
        info.setSoapAction(operation.getSoapAction());
        return info;
    }
}
