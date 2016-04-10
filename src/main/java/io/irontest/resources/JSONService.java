package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;

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

    @POST @Path("verifyassertion")
    public AssertionVerificationResult verifyAssertion(AssertionVerification assertionVerification) {
        Assertion assertion = assertionVerification.getAssertion();
        String assertionType = assertionVerification.getAssertion().getType();
        AssertionVerifier assertionVerifier = assertionVerifierFactory.create(assertionType);
        AssertionVerificationResult result = assertionVerifier.verify(assertionVerification);
        result.setAssertionId(assertion.getId());
        return result;
    }
}
