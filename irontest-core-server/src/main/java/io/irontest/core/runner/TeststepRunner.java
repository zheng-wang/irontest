package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public abstract class TeststepRunner {
    private Teststep teststep;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;
    private TestcaseRunContext testcaseRunContext;    //  set only when running test case
    private Map<String, String> referenceableStringProperties;   //  set when running standalone test step or test case
    private Map<String, Endpoint> referenceableEndpointProperties;   //  set when running standalone test step or test case

    protected TeststepRunner() {}

    public BasicTeststepRun run() throws Exception {
        prepareTeststep();
        return run(teststep);
    }

    private void prepareTeststep() throws IOException {
        //  decrypt password in endpoint
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            endpoint.setPassword(utilsDAO.decryptEndpointPassword(endpoint.getPassword()));
        }

        //  fetch request binary if its type is file
        if (teststep.getRequestType() == TeststepRequestType.FILE) {
            teststep.setRequest(teststepDAO.getBinaryRequestById(teststep.getId()));
        }

        resolveReferenceableStringProperties();

        if (teststep.getEndpointProperty() != null) {
            teststep.setEndpoint(referenceableEndpointProperties.get(teststep.getEndpointProperty()));
            if (teststep.getEndpoint() == null) {
                throw new RuntimeException("Endpoint property " + teststep.getEndpointProperty() + " not defined or is null.");
            }
        }
    }

    /**
     * Resolve as many string property references as possible. For unresolved references, throw exception in the end.
     * @throws IOException
     */
    private void resolveReferenceableStringProperties() throws IOException {
        List<String> undefinedStringProperties = new ArrayList<String>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

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

    protected void setTeststepDAO(TeststepDAO teststepDAO) {
        this.teststepDAO = teststepDAO;
    }

    protected Teststep getTeststep() {
        return teststep;
    }

    protected void setUtilsDAO(UtilsDAO utilsDAO) {
        this.utilsDAO = utilsDAO;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    protected void setTestcaseRunContext(TestcaseRunContext testcaseRunContext) {
        this.testcaseRunContext = testcaseRunContext;
    }

    protected void setReferenceableStringProperties(Map<String, String> referenceableStringProperties) {
        this.referenceableStringProperties = referenceableStringProperties;
    }

    public void setReferenceableEndpointProperties(Map<String, Endpoint> referenceableEndpointProperties) {
        this.referenceableEndpointProperties = referenceableEndpointProperties;
    }
}
