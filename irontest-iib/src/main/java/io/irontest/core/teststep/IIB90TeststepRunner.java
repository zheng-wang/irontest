package io.irontest.core.teststep;

import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import com.ibm.broker.config.proxy.LocalBrokerConnectionParameters;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.MQConnectionMode;
import io.irontest.models.endpoint.MQEndpointProperties;

public class IIB90TeststepRunner extends IIBTeststepRunnerBase {

    public IIB90TeststepRunner(Endpoint endpoint) {
        MQEndpointProperties endpointProperties = (MQEndpointProperties) endpoint.getOtherProperties();

        //  for connecting to IIB 9.0 integration node
        BrokerConnectionParameters bcp = null;
        if (endpointProperties.getConnectionMode() == MQConnectionMode.BINDINGS) {
            bcp = new LocalBrokerConnectionParameters(endpointProperties.getQueueManagerName());
        } else {
            bcp = new MQBrokerConnectionParameters(
                    endpoint.getHost(), endpoint.getPort(), endpointProperties.getQueueManagerName());
            ((MQBrokerConnectionParameters) bcp).setAdvancedConnectionParameters(
                    endpointProperties.getSvrConnChannelName(), null, null, -1, -1, null);
        }

        setBrokerConnectionParameters(bcp);
    }
}
