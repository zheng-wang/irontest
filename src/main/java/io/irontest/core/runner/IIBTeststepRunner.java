package io.irontest.core.runner;

import com.ibm.broker.config.proxy.*;
import io.irontest.models.Teststep;

import java.util.Date;

/**
 * Created by Zheng on 25/05/2016.
 */
public class IIBTeststepRunner implements TeststepRunner {
    public Object run(Teststep teststep) throws Exception {
        String hostname = "localhost";
        int port = 1410;
        String qmgr = "QM1";
        String channelName = "channel1";
        String egName = "default";
        String messageFlowName = "flow1";
        BrokerProxy b = null;
        try {
            MQBrokerConnectionParameters bcp = new MQBrokerConnectionParameters(hostname, port, qmgr);
            bcp.setAdvancedConnectionParameters(channelName, null, null, -1, -1, null);
            b = BrokerProxy.getInstance(bcp);
            b.setSynchronous(60 * 1000);
            String brokerName = b.getName();
            System.out.println("Broker '" + brokerName + "' is available!");
            System.out.println("Session ID: " + bcp.getSessionIDString());
            ExecutionGroupProxy egProxy = b.getExecutionGroupByName(egName);
            System.out.println("EG: " + egProxy.getName());

            MessageFlowProxy messageFlowProxy = egProxy.getMessageFlowByName(messageFlowName);
            System.out.println("Message flow: " + messageFlowProxy.getName());

            System.out.println(new Date());
            messageFlowProxy.start();
            System.out.println(new Date());


            b.disconnect();
        } catch (ConfigManagerProxyException ex) {
            System.out.println("Broker is NOT available because " + ex);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        IIBTeststepRunner runner = new IIBTeststepRunner();
        runner.run(null);
    }
}