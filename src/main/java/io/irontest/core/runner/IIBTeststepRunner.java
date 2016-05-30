package io.irontest.core.runner;

import com.ibm.broker.config.proxy.*;
import io.irontest.models.IIBEndpointProperties;
import io.irontest.models.IIBTeststepProperties;
import io.irontest.models.Teststep;

/**
 * Created by Zheng on 25/05/2016.
 */
public class IIBTeststepRunner implements TeststepRunner {
    public Object run(Teststep teststep) throws ConfigManagerProxyLoggedException,
            ConfigManagerProxyPropertyNotInitializedException {
        IIBEndpointProperties endpointProperties = (IIBEndpointProperties) teststep.getEndpoint().getOtherProperties();
        IIBTeststepProperties teststepProperties = (IIBTeststepProperties) teststep.getOtherProperties();
        MQBrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
                endpointProperties.getHost(), endpointProperties.getPort(), endpointProperties.getQueueManagerName());
        bcp.setAdvancedConnectionParameters(endpointProperties.getSvrConnChannelName(), null, null, -1, -1, null);
        BrokerProxy brokerProxy = null;
        try {
            //  connect to the broker
            brokerProxy = BrokerProxy.getInstance(bcp);
            brokerProxy.setSynchronous(60 * 1000);    //  do everything synchronously
            String integrationNodeName = brokerProxy.getName();

            //  get message flow proxy
            ExecutionGroupProxy egProxy = brokerProxy.getExecutionGroupByName(
                    teststepProperties.getIntegrationServerName());
            if (egProxy == null) {
                throw new RuntimeException("Execution group " + teststepProperties.getIntegrationServerName() +
                        " does not exist on broker " + integrationNodeName);
            }
            MessageFlowProxy messageFlowProxy = egProxy.getMessageFlowByName(teststepProperties.getMessageFlowName());
            if (messageFlowProxy == null) {
                throw new RuntimeException("Message flow " + teststepProperties.getMessageFlowName() +
                        " does not exist on execution group " + teststepProperties.getIntegrationServerName());
            }

            //  do the specified action
            if (IIBTeststepProperties.ACTION_TYPE_START.equals(teststepProperties.getAction())) {
                messageFlowProxy.start();
            } else if (IIBTeststepProperties.ACTION_TYPE_STOP.equals(teststepProperties.getAction())) {
                messageFlowProxy.stop();
            }
        } finally {
            if (brokerProxy != null) {
                brokerProxy.disconnect();
            }
        }

        return true;
    }
}