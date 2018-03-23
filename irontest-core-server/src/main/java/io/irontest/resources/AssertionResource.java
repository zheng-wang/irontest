package io.irontest.resources;

import io.irontest.core.assertion.AssertionVerifier;
import io.irontest.core.assertion.AssertionVerifierFactory;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UserDefinedPropertyDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.DataTable;
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
    private final TeststepDAO teststepDAO;
    private final UtilsDAO utilsDAO;

    public AssertionResource(UserDefinedPropertyDAO udpDAO, TeststepDAO teststepDAO, UtilsDAO utilsDAO) {
        this.udpDAO = udpDAO;
        this.teststepDAO = teststepDAO;
        this.utilsDAO = utilsDAO;
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

        //  gather referenceable string properties
        List<UserDefinedProperty> testcaseUDPs = udpDAO.findTestcaseUDPsByTeststepId(assertion.getTeststepId());
        Map<String, String> referenceableStringProperties = IronTestUtils.udpListToMap(testcaseUDPs);
        DataTable dataTable = utilsDAO.getTestcaseDataTable(teststepDAO.findTestcaseIdById(assertion.getTeststepId()), true);
        if (dataTable != null && dataTable.getRows().size() == 1) {
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(0));
        }
        AssertionVerifier assertionVerifier = AssertionVerifierFactory.getInstance().create(
                assertion.getType(), referenceableStringProperties);
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