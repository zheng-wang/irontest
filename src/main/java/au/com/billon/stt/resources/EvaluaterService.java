package au.com.billon.stt.resources;

import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;
import au.com.billon.stt.models.WSDLBinding;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * This is not a REST resource, but an RPC service via JSON.
 * Created by Zheng on 27/07/2015.
 */
@Path("/evaluator") @Produces({ MediaType.APPLICATION_JSON })
public class EvaluaterService {
    public EvaluaterService() {}

    @POST
    public EvaluationResponse evaluate(EvaluationRequest request) {
        EvaluationResponse response = new EvaluationResponse();
        if (EvaluationRequest.EVALUATION_TYPE_XPATH.equals(request.getType())) {
            //  TODO evaluate XPath
            response.setValue("value");
        }

        return response;
    }
}
