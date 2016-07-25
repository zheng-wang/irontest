package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.EndpointDAO;
import io.irontest.models.Endpoint;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerification;
import io.irontest.models.assertion.AssertionVerificationResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * A pseudo JSON RPC service for hosting action oriented browser-server interactions.
 * It is not based on JSON-RPC spec, nor is it a REST resource.
 * Created by Zheng on 27/07/2015.
 */
@Path("/jsonservice") @Produces({ MediaType.APPLICATION_JSON })
public class JSONService {
    private AssertionVerifierFactory assertionVerifierFactory;
    private EndpointDAO endpointDAO;

    public JSONService(AssertionVerifierFactory assertionVerifierFactory, EndpointDAO endpointDAO) {
        this.assertionVerifierFactory = assertionVerifierFactory;
        this.endpointDAO = endpointDAO;
    }

    @POST @Path("verifyassertion")
    public AssertionVerificationResult verifyAssertion(AssertionVerification assertionVerification) throws InterruptedException {
        Thread.sleep(100);  //  workaround for Chrome 44 to 48's 'Failed to load response data' problem (no such problem in Chrome 49)
        Assertion assertion = assertionVerification.getAssertion();
        String assertionType = assertionVerification.getAssertion().getType();
        AssertionVerifier assertionVerifier = assertionVerifierFactory.create(assertionType);
        AssertionVerificationResult result = assertionVerifier.verify(
                assertionVerification.getAssertion(), assertionVerification.getInput());
        result.setAssertionId(assertion.getId());
        return result;
    }

    @GET @Path("findManagedEndpointsByType")
    public List<Endpoint> findManagedEndpointsByType(@QueryParam("type") String endpointType) {
        return endpointDAO.findManagedEndpointsByType(endpointType);
    }
}
