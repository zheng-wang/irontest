package io.irontest.resources;

import io.irontest.models.WSDLBinding;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
@Path("/wsdls") @Produces({ MediaType.APPLICATION_JSON })
public class WSDLResource {
    public WSDLResource() {}

    @GET @Path("/anywsdl/operations")
    public List<WSDLBinding> getWSDLOperations(@QueryParam("wsdlUrl") String wsdlUrl) {
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
}
