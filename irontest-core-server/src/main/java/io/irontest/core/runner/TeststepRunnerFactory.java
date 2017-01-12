package io.irontest.core.runner;

import io.irontest.db.TeststepDAO;
import io.irontest.db.UtilsDAO;
import io.irontest.models.Teststep;

import java.lang.reflect.Constructor;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class TeststepRunnerFactory {
    private static TeststepRunnerFactory instance;

    private TeststepRunnerFactory() { }

    public static synchronized TeststepRunnerFactory getInstance() {
        if ( instance == null ) {
            instance = new TeststepRunnerFactory();
        }
        return instance;
    }

    public TeststepRunner newTeststepRunner(Teststep teststep, TeststepDAO teststepDAO, UtilsDAO utisDAO,
                                            TestcaseRunContext testcaseRunContext) {
        TeststepRunner runner = null;
        try {
            Class runnerClass = Class.forName("io.irontest.core.runner." + teststep.getType() + "TeststepRunner");
            Constructor<TeststepRunner> constructor = runnerClass.getConstructor();
            runner = constructor.newInstance();
            runner.setTeststep(teststep);
            runner.setTeststepDAO(teststepDAO);
            runner.setUtilsDAO(utisDAO);
            runner.setTestcaseRunContext(testcaseRunContext);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate test step runner.", e);
        }

        return runner;
    }
}
