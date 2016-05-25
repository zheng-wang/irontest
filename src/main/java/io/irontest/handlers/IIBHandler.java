package io.irontest.handlers;

import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyException;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import io.irontest.models.Endpoint;

/**
 * Created by Zheng on 25/05/2016.
 */
public class IIBHandler implements IronTestHandler {
    public Object invoke(String request, Endpoint endpoint) throws Exception {
        String hostname = "localhost";
        int port = 1410;
        String qmgr = "QM1";
        String brokerName = "Broker1";
        BrokerProxy b = null;
        try {
            BrokerConnectionParameters bcp =
                    new MQBrokerConnectionParameters(hostname, port, qmgr);
            //b = BrokerProxy.getInstance(bcp);
            b = BrokerProxy.getLocalInstance(brokerName);
            brokerName = b.getName();

            System.out.println("Broker '" + brokerName + "' is available!");
            b.disconnect();
        } catch (ConfigManagerProxyException ex) {
            System.out.println("Broker is NOT available because " + ex);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        IIBHandler handler = new IIBHandler();
        handler.invoke(null, null);
    }
}
