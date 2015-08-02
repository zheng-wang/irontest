package au.com.billon.stt.resources;

import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.models.AssertionVerificationRequest;
import au.com.billon.stt.models.AssertionVerificationResponse;
import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A pseudo JSON RPC service for hosting action oriented browser-server interactions.
 * It is not based on JSON-RPC spec, nor is it a REST resource.
 * Created by Zheng on 27/07/2015.
 */
@Path("/jsonservice") @Produces({ MediaType.APPLICATION_JSON })
public class JSONService {
    private EvaluatorFactory factory;

    public JSONService(EvaluatorFactory factory) {
        this.factory = factory;
    }

    @POST @Path("evaluate")
    public EvaluationResponse evaluate(EvaluationRequest request) {
        return factory.createEvaluator(request).evaluate();
    }

    @POST @Path("verifyassertion")
    public AssertionVerificationResponse verifyAssertion(AssertionVerificationRequest request) {
        return null;
    }
}
