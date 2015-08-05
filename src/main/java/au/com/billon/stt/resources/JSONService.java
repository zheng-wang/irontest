package au.com.billon.stt.resources;

import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.models.*;

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
    private EvaluatorFactory evaluatorFactory;

    public JSONService(EvaluatorFactory evaluatorFactory) {
        this.evaluatorFactory = evaluatorFactory;
    }

    @POST @Path("evaluate")
    public EvaluationResponse evaluate(EvaluationRequest request) {
        return evaluatorFactory.createEvaluator(request).evaluate();
    }

    @POST @Path("verifyassertion")
    public Assertion verifyAssertion(Assertion assertion) {
        if (Assertion.ASSERTION_TYPE_XPATH.equals(assertion.getType())) {
            AssertionVerification verification = assertion.getVerification();
            XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getProperties();
            EvaluationRequest evaluationRequest = new EvaluationRequest(
                    assertion.getType(), assertionProperties.getxPath(), verification.getInput(),
                    new XPathEvaluationRequestProperties(assertionProperties.getNamespacePrefixes()));
            EvaluationResponse evaluationResponse = evaluatorFactory.createEvaluator(evaluationRequest).evaluate();
            verification.setPassed(evaluationResponse.getError() == null &&
                    assertionProperties.getExpectedValue().equals(evaluationResponse.getActualValue()));
            verification.setError(evaluationResponse.getError());
            verification.setActualValue(evaluationResponse.getActualValue());
        }
        return assertion;
    }
}
