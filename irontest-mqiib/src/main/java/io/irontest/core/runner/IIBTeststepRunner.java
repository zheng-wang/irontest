package io.irontest.core.runner;

import com.ibm.broker.config.proxy.*;
import io.irontest.models.IIBTeststepProperties;
import io.irontest.models.MQIIBEndpointProperties;
import io.irontest.models.Teststep;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Zheng on 25/05/2016.
 */
public class IIBTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IIBTeststepRunner.class);
    private static final int ACTIVITY_LOG_POLLING_TIMEOUT = 60;    // in seconds

    protected Object run(Teststep teststep) throws Exception {
        String action = teststep.getAction();
        if (action == null) {
            throw new Exception("Action not specified.");
        }

        MQIIBEndpointProperties endpointProperties = (MQIIBEndpointProperties) teststep.getEndpoint().getOtherProperties();
        IIBTeststepProperties teststepProperties = (IIBTeststepProperties) teststep.getOtherProperties();
        MQBrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
                endpointProperties.getHost(), endpointProperties.getPort(), endpointProperties.getQueueManagerName());
        bcp.setAdvancedConnectionParameters(endpointProperties.getSvrConnChannelName(), null, null, -1, -1, null);
        BrokerProxy brokerProxy = null;
        try {
            //  connect to the broker
            brokerProxy = BrokerProxy.getInstance(bcp);
            brokerProxy.setSynchronous(90 * 1000);    //  do everything synchronously
            String integrationNodeName = brokerProxy.getName();

            //  get message flow proxy
            ExecutionGroupProxy egProxy = brokerProxy.getExecutionGroupByName(
                    teststepProperties.getIntegrationServerName());
            if (egProxy == null) {
                throw new Exception("EG \"" + teststepProperties.getIntegrationServerName() +
                        "\" not found on broker \"" + integrationNodeName + "\".");
            } else if (!egProxy.isRunning()) {
                throw new Exception("EG \"" + teststepProperties.getIntegrationServerName() +
                        "\" not running.");
            }
            MessageFlowProxy messageFlowProxy = egProxy.getMessageFlowByName(teststepProperties.getMessageFlowName());
            if (messageFlowProxy == null) {
                throw new Exception("Message flow \"" + teststepProperties.getMessageFlowName() +
                        "\" not found on EG \"" + teststepProperties.getIntegrationServerName() + "\".");
            }

            //  do the specified action
            if (Teststep.ACTION_START.equals(action)) {
                messageFlowProxy.start();
            } else if (Teststep.ACTION_STOP.equals(action)) {
                messageFlowProxy.stop();
            } else if (Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(action)) {
                waitForProcessingCompletion(messageFlowProxy);
            } else {
                throw new Exception("Unrecognized action " + action);
            }
        } finally {
            if (brokerProxy != null) {
                brokerProxy.disconnect();
            }
        }

        return null;
    }

    private void waitForProcessingCompletion(MessageFlowProxy messageFlowProxy) throws Exception {
        TestcaseRunContext testcaseRunContext = getTestcaseRunContext();
        Date pollingEndTime = DateUtils.addSeconds(new Date(), ACTIVITY_LOG_POLLING_TIMEOUT);
        ActivityLogEntry processingCompletionSignal = null;
        while (System.currentTimeMillis() < pollingEndTime.getTime()) {
            ActivityLogProxy activityLogProxy = messageFlowProxy.getActivityLog();
            if (activityLogProxy != null) {
                for (int i = 1; i <= activityLogProxy.getSize(); i++) {
                    ActivityLogEntry logEntry = activityLogProxy.getLogEntry(i);
                    if (11504 == logEntry.getMessageNumber() &&
                            logEntry.getTimestamp().after(testcaseRunContext.getTestcaseRunStartTime())) {
                        processingCompletionSignal = logEntry;
                        break;
                    }
                }
            }
            if (processingCompletionSignal != null) {
                break;
            } else {
                Thread.sleep(1000);
            }
        }
        if (processingCompletionSignal == null) {
            throw new Exception("Message flow activity log polling timeout. No processing completion signal found.");
        } else {
            LOGGER.info("Message flow processing completion signal found. " + processingCompletionSignal.toString());
        }
    }
}