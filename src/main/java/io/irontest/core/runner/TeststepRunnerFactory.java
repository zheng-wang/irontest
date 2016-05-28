package io.irontest.core.runner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class TeststepRunnerFactory {
    private static TeststepRunnerFactory instance;

    private Map<String, TeststepRunner> runners = new HashMap<String, TeststepRunner>();

    private TeststepRunnerFactory() { }

    public static synchronized TeststepRunnerFactory getInstance() {
        if ( instance == null ) {
            instance = new TeststepRunnerFactory();
        }
        return instance;
    }

    public TeststepRunner getTeststepRunner(String runnerName) {
        TeststepRunner runner = null;
        if (runnerName != null) {
            runner = runners.get(runnerName);
            if (runner == null) {
                try {
                    Class runnerClass = Class.forName("io.irontest.core.runner." + runnerName);
                    runner = (TeststepRunner) runnerClass.newInstance();
                    runners.put(runnerName, runner);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to instantiate test step runner.", e);
                }
            }
        }

        return runner;
    }
}
