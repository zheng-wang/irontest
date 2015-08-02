package au.com.billon.stt.resources;

import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This is not a REST resource, but an RPC service via JSON.
 * Created by Zheng on 27/07/2015.
 */
@Path("/evaluator") @Produces({ MediaType.APPLICATION_JSON })
public class EvaluatorService {
    private EvaluatorFactory factory;

    public EvaluatorService(EvaluatorFactory factory) {
        this.factory = factory;
    }

    @POST
    public EvaluationResponse evaluate(EvaluationRequest request) {
        return factory.createEvaluator(request).evaluate();
    }
}
