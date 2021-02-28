package io.irontest.core.teststep;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.irontest.core.MapValueLookup;
import io.irontest.core.testcase.TestcaseRunContext;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.JMSEndpointProperties;
import io.irontest.models.teststep.APIRequest;
import io.irontest.models.teststep.HTTPStubsSetupTeststepProperties;
import io.irontest.models.teststep.Teststep;
import io.irontest.models.teststep.TeststepRequestType;
import io.irontest.utils.IronTestUtils;
import org.apache.commons.text.StrSubstitutor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeststepRunnerFactory {
    private static TeststepRunnerFactory instance = new TeststepRunnerFactory();

    private TeststepRunnerFactory() { }

    public static TeststepRunnerFactory getInstance() {
        return instance;
    }

    private String resolveTeststepRunnerClassName(Teststep teststep) {
        if (Teststep.TYPE_JMS.equals(teststep.getType())) {
            JMSEndpointProperties endpointProperties = (JMSEndpointProperties) teststep.getEndpoint().getOtherProperties();
            return "io.irontest.core.teststep." + teststep.getType() + endpointProperties.getJmsProvider() + "TeststepRunner";
        } else {
            return "io.irontest.core.teststep." + teststep.getType() + "TeststepRunner";
        }
    }

    /**
     * This method modifies content of the propertyExtractor object.
     * @param teststep
     * @param utilsDAO
     * @param referenceableStringProperties
     * @param referenceableEndpointProperties
     * @param testcaseRunContext
     * @return
     */
    public TeststepRunner newTeststepRunner(Teststep teststep, UtilsDAO utilsDAO,
                                            Map<String, String> referenceableStringProperties,
                                            Map<String, Endpoint> referenceableEndpointProperties, TestcaseRunContext testcaseRunContext) throws Exception {
        TeststepRunner runner;
        Class runnerClass = Class.forName(resolveTeststepRunnerClassName(teststep));
        Constructor<TeststepRunner> constructor = runnerClass.getConstructor();
        runner = constructor.newInstance();

        resolveReferenceableStringProperties(teststep, referenceableStringProperties);

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
            runner.setDecryptedEndpointPassword(utilsDAO.decryptEndpointPassword(endpoint.getPassword()));
        }

        runner.setTeststep(teststep);

        runner.setTestcaseRunContext(testcaseRunContext);

        return runner;
    }

    /**
     * Resolve as many string property references as possible. For unresolved references, throw exception in the end.
     * @throws IOException
     */
    private void resolveReferenceableStringProperties(Teststep teststep, Map<String, String> referenceableStringProperties) throws IOException {
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

        //  resolve property references in teststep.apiRequest (null safe)
        String apiRequestJSON = objectMapper.writeValueAsString(teststep.getApiRequest());
        propertyReferenceResolver = new MapValueLookup(referenceableStringProperties, true);
        String resolvedApiRequestJSON = new StrSubstitutor(propertyReferenceResolver).replace(apiRequestJSON);
        undefinedStringProperties.addAll(propertyReferenceResolver.getUnfoundKeys());
        APIRequest resolvedApiRequest = objectMapper.readValue(resolvedApiRequestJSON, APIRequest.class);
        teststep.setApiRequest(resolvedApiRequest);

        if (!undefinedStringProperties.isEmpty()) {
            throw new RuntimeException("String properties " + undefinedStringProperties + " not defined.");
        }
    }
}
