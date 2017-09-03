package io.irontest.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.UserDefinedProperty;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import org.apache.commons.text.StrLookup;
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

        //  resolve UDP references in the teststep.otherProperties
        //  resolve as many as possible; for unresolved property references, throw exception later
        String otherPropertiesStr = new ObjectMapper().writeValueAsString(this.teststep.getOtherProperties());
        final List<String> undefinedProperties = new ArrayList<String>();
        StrSubstitutor sub = new StrSubstitutor(new StrLookup<String>() {
            @Override
            public String lookup(String key) {
                key = key.trim();
                for (UserDefinedProperty udp: testcaseUDPs) {
                    if (key.equals(udp.getName())) {
                        return udp.getValue();
                    }
                }
                undefinedProperties.add(key);
                return null;    //  returning null preserves the property reference untouched (not replaced) in the template string
            }
        });
        String resolvedOtherPropertiesStr = sub.replace(otherPropertiesStr);
        String tempStepJSON = "{\"type\":\"" + teststep.getType() + "\",\"otherProperties\":" +
                resolvedOtherPropertiesStr + "}";
        Teststep tempStep = new ObjectMapper().readValue(tempStepJSON, Teststep.class);
        this.teststep.setOtherProperties(tempStep.getOtherProperties());

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
