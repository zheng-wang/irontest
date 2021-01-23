package io.irontest.core.teststep;

import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.IIBEndpointProperties;
import org.eclipse.jetty.util.log.Log;

import java.lang.reflect.Constructor;

public class IIB100TeststepRunner extends IIBTeststepRunnerBase {
    //  disable IIB 10 IntegrationAPI.jar's jetty logging (which pollutes StdErr)
    static {
        Log.setLog(new NoLogging());
    }

    private static class NoLogging implements org.eclipse.jetty.util.log.Logger {
        @Override public String getName() { return null; }
        @Override public void warn(String msg, Object... args) {}
        @Override public void warn(Throwable thrown) {}
        @Override public void warn(String msg, Throwable thrown) {}
        @Override public void info(String msg, Object... args) {}
        @Override public void info(Throwable thrown) {}
        @Override public void info(String msg, Throwable thrown) {}
        @Override public boolean isDebugEnabled() { return false; }
        @Override public void setDebugEnabled(boolean enabled) {}
        @Override public void debug(String msg, Object... args) {}
        @Override public void debug(String msg, long value) {}
        @Override public void debug(Throwable thrown) {}
        @Override public void debug(String msg, Throwable thrown) {}
        @Override public org.eclipse.jetty.util.log.Logger getLogger(String name) { return this; }
        @Override public void ignore(Throwable ignored) {}
    }

    public IIB100TeststepRunner(Endpoint endpoint, String decryptedEndpointPassword) throws Exception {
        IIBEndpointProperties endpointProperties = (IIBEndpointProperties) endpoint.getOtherProperties();

        //  for connecting to IIB 10.0 integration node
        //  use Class.forName so that the code can be compiled with either IIB 9.0 or IIB 10.0 integration API jars
        Class clazz = Class.forName("com.ibm.broker.config.proxy.IntegrationNodeConnectionParameters");
        Constructor<BrokerConnectionParameters> constructor = clazz.getConstructor(
                String.class, Integer.TYPE, String.class, String.class, Boolean.TYPE);
        BrokerConnectionParameters bcp = constructor.newInstance(
                    endpoint.getHost(), endpoint.getPort(), endpoint.getUsername(),
                    decryptedEndpointPassword, endpointProperties.isUseSSL());
        setBrokerConnectionParameters(bcp);
    }
}
