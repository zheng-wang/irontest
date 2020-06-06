package io.irontest.core.teststep;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.Teststep;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is actually a factory and delegator instead of the actual runner.
 */
public class IIBTeststepRunner extends TeststepRunner {
    private static IIBTeststepRunnerClassLoader iib100ClassLoader;
    private static IIBTeststepRunnerClassLoader iib90ClassLoader;
    static {
        URL[] iib100URLs;
        URL[] iib90URLs;
        File userDir = new File(System.getProperty("user.dir"));
        try {
            iib100URLs = new URL[] {
                    new File(userDir, "lib/iib/v100/IntegrationAPI.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/jetty-client.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/jetty-io.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/jetty-util.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-api.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-client.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v100/websocket-common.jar").toURI().toURL()
            };
            iib90URLs = new URL[] {
                    new File(userDir, "lib/iib/v90/ibmjsseprovider2.jar").toURI().toURL(),
                    new File(userDir, "lib/iib/v90/ConfigManagerProxy.jar").toURI().toURL()
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to initialize " + IIBTeststepRunner.class.getName(), e);
        }
        iib100ClassLoader = new IIBTeststepRunnerClassLoader(iib100URLs, IIBTeststepRunner.class.getClassLoader());
        iib90ClassLoader = new IIBTeststepRunnerClassLoader(iib90URLs, IIBTeststepRunner.class.getClassLoader());
    }

    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        Endpoint endpoint = teststep.getEndpoint();
        String actualRunnerClassName;
        ClassLoader classLoader;
        TeststepRunner actualRunner;
        if (Endpoint.TYPE_IIB.equals(endpoint.getType())) {    //  it is an IIB 10.0 endpoint
            actualRunnerClassName = "io.irontest.core.teststep.IIB100TeststepRunner";
            classLoader = iib100ClassLoader;
            Class actualRunnerClass = Class.forName(actualRunnerClassName, false, classLoader);
            Constructor<TeststepRunner> constructor = actualRunnerClass.getConstructor(Endpoint.class, String.class);
            actualRunner = constructor.newInstance(endpoint, getDecryptedEndpointPassword());
        } else {    //  it is an IIB 9.0 endpoint
            actualRunnerClassName = "io.irontest.core.teststep.IIB90TeststepRunner";
            classLoader = iib90ClassLoader;
            Class actualRunnerClass = Class.forName(actualRunnerClassName, false, classLoader);
            Constructor<TeststepRunner> constructor = actualRunnerClass.getConstructor(Endpoint.class);
            actualRunner = constructor.newInstance(endpoint);
        }

        actualRunner.setTeststep(teststep);
        actualRunner.setTestcaseRunContext(getTestcaseRunContext());
        return actualRunner.run();
    }
}