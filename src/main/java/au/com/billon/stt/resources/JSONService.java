package au.com.billon.stt.resources;

import au.com.billon.stt.core.AssertionVerifierFactory;
import au.com.billon.stt.models.Assertion;

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
    private AssertionVerifierFactory assertionVerifierFactory;

    public JSONService(AssertionVerifierFactory assertionVerifierFactory) {
        this.assertionVerifierFactory = assertionVerifierFactory;
    }

//    @POST @Path("evaluate")
//    public EvaluationResponse evaluate(EvaluationRequest request) {
//        return evaluatorFactory.createEvaluator(request).evaluate();
//    }

    @POST @Path("verifyassertion")
    public Assertion verifyAssertion(Assertion assertion) {
        return assertionVerifierFactory.create(assertion.getType()).verify(assertion);
    }
}
