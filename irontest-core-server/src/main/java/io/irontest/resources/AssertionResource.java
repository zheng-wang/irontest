package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.models.TestResult;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.assertion.AssertionVerificationRequest;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.utils.IronTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 27/07/2015.
 */
@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class AssertionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionResource.class);

    private final UserDefinedPropertyDAO udpDAO;

    public AssertionResource(UserDefinedPropertyDAO udpDAO) {
        this.udpDAO = udpDAO;
    }

    /**
     * This is a stateless operation, i.e. not persisting anything in database.
     * @param assertionVerificationRequest
     * @return
     */
    @POST @Path("assertions/{assertionId}/verify")
    @PermitAll
    public AssertionVerificationResult verify(AssertionVerificationRequest assertionVerificationRequest) {
        Assertion assertion = assertionVerificationRequest.getAssertion();

        List<UserDefinedProperty> testcaseUDPs = udpDAO.findTestcaseUDPsByTeststepId(assertion.getTeststepId());
        Map<String, String> referenceableProperties = IronTestUtils.udpListToMap(testcaseUDPs);

        AssertionVerifier assertionVerifier = AssertionVerifierFactory.getInstance().create(
                assertion.getType(), referenceableProperties);
        AssertionVerificationResult result;
        try {
            result = assertionVerifier.verify(assertion, assertionVerificationRequest.getInput());
        } catch (Exception e) {
            LOGGER.error("Failed to verify assertion", e);
            result = new AssertionVerificationResult();
            result.setResult(TestResult.FAILED);
            result.setError(e.getMessage());
        }
        return result;
    }
}