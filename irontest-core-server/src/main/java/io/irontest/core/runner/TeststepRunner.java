package io.irontest.core.runner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.UDPValueLookup;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevor Li on 7/14/15.
 */
public abstract class TeststepRunner {
    private Teststep teststep;
    private TeststepDAO teststepDAO;
    private UtilsDAO utilsDAO;
    private TestcaseRunContext testcaseRunContext;    //  only set when running test case
    private List<UserDefinedProperty> testcaseUDPs;   //  set when running standalone test step or test case

    protected TeststepRunner() {}

    public BasicTeststepRun run() throws Exception {
        prepareTeststep();
        return run(teststep);
    }

    private void prepareTeststep() throws IOException {
        //  decrypt password in endpoint
        Endpoint endpoint = this.teststep.getEndpoint();
        if (endpoint != null && endpoint.getPassword() != null) {
            endpoint.setPassword(this.utilsDAO.decryptPassword(endpoint.getPassword()));
        }

        //  fetch request binary if its type is file
        if (this.teststep.getRequestType() == TeststepRequestType.FILE) {
            this.teststep.setRequest(this.teststepDAO.getBinaryRequestById(this.teststep.getId()));
        }

        resolveUDPReferences();
    }

    /**
     * Resolve as many UDP references as possible. For unresolved references, throw exception in the end.
     * @throws IOException
     */
    private void resolveUDPReferences() throws IOException {
        final List<String> undefinedProperties = new ArrayList<String>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        //  resolve UDP references in teststep.otherProperties
        String otherPropertiesJSON = objectMapper.writeValueAsString(this.teststep.getOtherProperties());
        UDPValueLookup propertyReferenceResolver = new UDPValueLookup(this.testcaseUDPs, true);
        String resolvedOtherPropertiesJSON = new StrSubstitutor(propertyReferenceResolver).replace(otherPropertiesJSON);
        undefinedProperties.addAll(propertyReferenceResolver.getUndefinedProperties());
        String tempStepJSON = "{\"type\":\"" + this.teststep.getType() + "\",\"otherProperties\":" +
                resolvedOtherPropertiesJSON + "}";
        Teststep tempStep = objectMapper.readValue(tempStepJSON, Teststep.class);
        this.teststep.setOtherProperties(tempStep.getOtherProperties());

        //  resolve UDP references in teststep.request (text type)
        if (teststep.getRequestType() == TeststepRequestType.TEXT) {
            propertyReferenceResolver = new UDPValueLookup(this.testcaseUDPs, false);
            this.teststep.setRequest(new StrSubstitutor(propertyReferenceResolver).replace(
                    (String) this.teststep.getRequest()));
            undefinedProperties.addAll(propertyReferenceResolver.getUndefinedProperties());
        }

        if (!undefinedProperties.isEmpty()) {
            throw new RuntimeException("Properties " + undefinedProperties + " are undefined.");
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

    protected void setTestcaseUDPs(List<UserDefinedProperty> testcaseUDPs) {
        this.testcaseUDPs = testcaseUDPs;
    }
}
