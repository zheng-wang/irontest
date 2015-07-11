package au.com.billon.stt.resources;

import au.com.billon.stt.models.Testcase;
import au.com.billon.stt.models.WSDLBinding;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
        System.out.println(wsdlUrl);
        List<WSDLBinding> result = new ArrayList<WSDLBinding>();
        result.add(new WSDLBinding("binding1"));
        result.add(new WSDLBinding("binding2"));
        return result;
    }
}
