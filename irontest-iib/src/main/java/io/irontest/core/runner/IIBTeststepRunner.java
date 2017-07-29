package io.irontest.core.runner;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.IIBEndpointProperties;
import io.irontest.models.endpoint.MQEndpointProperties;
import io.irontest.models.teststep.Teststep;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Zheng on 25/05/2016.
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

    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        Endpoint endpoint = teststep.getEndpoint();
        String actualRunnerClassName;
        ClassLoader classLoader;
        Class endpointPropertiesClass;
        if (Endpoint.TYPE_IIB.equals(endpoint.getType())) {    //  it is an IIB 10.0 endpoint
            actualRunnerClassName = "io.irontest.core.runner.IIB100TeststepRunner";
            classLoader = iib100ClassLoader;
            endpointPropertiesClass = IIBEndpointProperties.class;
        } else {    //  it is an IIB 9.0 endpoint
            actualRunnerClassName = "io.irontest.core.runner.IIB90TeststepRunner";
            classLoader = iib90ClassLoader;
            endpointPropertiesClass = MQEndpointProperties.class;
        }
        Class actualRunnerClass = Class.forName(actualRunnerClassName, false, classLoader);
        Constructor<TeststepRunner> constructor = actualRunnerClass.getConstructor(endpointPropertiesClass);
        TeststepRunner actualRunner = constructor.newInstance(endpoint.getOtherProperties());

        actualRunner.setTestcaseRunContext(getTestcaseRunContext());
        return actualRunner.run(teststep);
    }
}