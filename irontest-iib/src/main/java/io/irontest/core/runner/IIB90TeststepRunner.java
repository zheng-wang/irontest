package io.irontest.core.runner;

import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import io.irontest.models.endpoint.MQEndpointProperties;

/**
 * Created by Zheng on 25/07/2017.
 */
public class IIB90TeststepRunner extends IIBTeststepRunnerBase {

    public IIB90TeststepRunner(MQEndpointProperties endpointProperties) {
        //  for connecting to IIB 9.0 integration node
        MQBrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
                endpointProperties.getHost(), endpointProperties.getPort(), endpointProperties.getQueueManagerName());
        bcp.setAdvancedConnectionParameters(
                endpointProperties.getSvrConnChannelName(), null, null, -1, -1, null);
        setBrokerConnectionParameters(bcp);
    }
}
