package io.irontest.core.runner;

import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class TeststepRunnerFactory {
    private static TeststepRunnerFactory instance = new TeststepRunnerFactory();

    private TeststepRunnerFactory() { }

    public static TeststepRunnerFactory getInstance() {
        return instance;
    }

    public TeststepRunner newTeststepRunner(Teststep teststep, TeststepDAO teststepDAO, UtilsDAO utilsDAO,
                                            Map<String, String> referenceableStringProperties,
                                            Map<String, Endpoint> referenceableEndpointProperties, TestcaseRunContext testcaseRunContext) {
        TeststepRunner runner;
        try {
            Class runnerClass = Class.forName("io.irontest.core.runner." + teststep.getType() + "TeststepRunner");
            Constructor<TeststepRunner> constructor = runnerClass.getConstructor();
            runner = constructor.newInstance();
            runner.setTeststep(teststep);
            runner.setTeststepDAO(teststepDAO);
            runner.setUtilsDAO(utilsDAO);
            runner.setReferenceableStringProperties(referenceableStringProperties);
            runner.setReferenceableEndpointProperties(referenceableEndpointProperties);
            runner.setTestcaseRunContext(testcaseRunContext);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate test step runner.", e);
        }

        return runner;
    }
}
