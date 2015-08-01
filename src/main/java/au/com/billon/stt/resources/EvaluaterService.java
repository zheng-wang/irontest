package au.com.billon.stt.resources;

import au.com.billon.stt.core.Evaluator;
import au.com.billon.stt.models.EvaluationRequest;
import au.com.billon.stt.models.EvaluationResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * This is not a REST resource, but an RPC service via JSON.
 * Created by Zheng on 27/07/2015.
 */
@Path("/evaluator") @Produces({ MediaType.APPLICATION_JSON })
public class EvaluaterService {
    private Evaluator evaluator;

    public EvaluaterService(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @POST
    public EvaluationResponse evaluate(EvaluationRequest request) {
        return evaluator.evaluate(request);
    }
}
