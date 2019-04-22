package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TeststepRunner {
    private Teststep teststep;
    private String decryptedEndpointPassword;
    private UtilsDAO utilsDAO;
    private TestcaseRunContext testcaseRunContext;    //  set only when running test case
    private Map<String, String> referenceableStringProperties;   //  set when running standalone test step or test case
    private Map<String, Endpoint> referenceableEndpointProperties;   //  set when running standalone test step or test case

    protected TeststepRunner() {}

    public BasicTeststepRun run() throws Exception {
        prepareTeststep();
        return run(teststep);
    }

    /**
     * This method modifies the content of teststep object.
     * @throws IOException
     */
    private void prepareTeststep() throws IOException {
        resolveReferenceableStringProperties();

        //  special processing for otherProperties that contains HTTPStubMapping objects
        //  must do this after resolving referenceable string properties
        if (teststep.getOtherProperties() instanceof HTTPStubsSetupTeststepProperties) {
            HTTPStubsSetupTeststepProperties httpStubsSetupTeststepProperties =
                    (HTTPStubsSetupTeststepProperties) teststep.getOtherProperties();
            IronTestUtils.substituteRequestBodyMainPatternValue(httpStubsSetupTeststepProperties.getHttpStubMappings());
        }

        //  resolve endpoint property if set on test step
        if (teststep.getEndpointProperty() != null) {
            teststep.setEndpoint(referenceableEndpointProperties.get(teststep.getEndpointProperty()));
            if (teststep.getEndpoint() == null) {
                throw new RuntimeException("Endpoint property " + teststep.getEndpointProperty() + " not defined or is null.");
            }
        }

        //  decrypt password from endpoint
        //  not modifying the endpoint object. Reasons
        //    1. Avoid the decrypted password leaking out of this runner (moving around with the Endpoint object)
        //    2. Avoid affecting other step runs when the endpoint object comes from a referenceable property (like from data table)
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            decryptedEndpointPassword = utilsDAO.decryptEndpointPassword(endpoint.getPassword());
        }
    }

    /**
     * Resolve as many string property references as possible. For unresolved references, throw exception in the end.
     * @throws IOException
     */
    private void resolveReferenceableStringProperties() throws IOException {
        List<String> undefinedStringProperties = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        IronTestUtils.addMixInsForWireMock(objectMapper);

        //  resolve property references in teststep.otherProperties
        String otherPropertiesJSON = objectMapper.writeValueAsString(teststep.getOtherProperties());
        MapValueLookup propertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);
        String resolvedOtherPropertiesJSON = new StrSubstitutor(propertyReferenceResolver).replace(otherPropertiesJSON);
        undefinedStringProperties.addAll(propertyReferenceResolver.getUnfoundKeys());
        String tempStepJSON = "{\"type\":\"" + teststep.getType() + "\",\"otherProperties\":" +
                resolvedOtherPropertiesJSON + "}";
        Teststep tempStep = objectMapper.readValue(tempStepJSON, Teststep.class);
        teststep.setOtherProperties(tempStep.getOtherProperties());

        //  resolve property references in teststep.request (text type)
        if (teststep.getRequestType() == TeststepRequestType.TEXT) {
            propertyReferenceResolver = new MapValueLookup(referenceableStringProperties, false);
            teststep.setRequest(new StrSubstitutor(propertyReferenceResolver).replace(
                    (String) teststep.getRequest()));
            undefinedStringProperties.addAll(propertyReferenceResolver.getUnfoundKeys());
        }

        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }
    }

    protected abstract BasicTeststepRun run(Teststep teststep) throws Exception;

    protected void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    protected Teststep getTeststep() {
        return teststep;
    }

    protected String getDecryptedEndpointPassword() {
        return decryptedEndpointPassword;
    }

    void setUtilsDAO(UtilsDAO utilsDAO) {
        this.utilsDAO = utilsDAO;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    void setTestcaseRunContext(TestcaseRunContext testcaseRunContext) {
        this.testcaseRunContext = testcaseRunContext;
    }

    void setReferenceableStringProperties(Map<String, String> referenceableStringProperties) {
        this.referenceableStringProperties = referenceableStringProperties;
    }

    void setReferenceableEndpointProperties(Map<String, Endpoint> referenceableEndpointProperties) {
        this.referenceableEndpointProperties = referenceableEndpointProperties;
    }
}
