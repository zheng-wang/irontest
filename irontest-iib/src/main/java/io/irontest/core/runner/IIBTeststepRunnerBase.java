package io.irontest.core.runner;

import com.ibm.broker.config.proxy.*;
import io.irontest.models.teststep.IIBTeststepProperties;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Zheng on 25/07/2017.
 */
public class IIBTeststepRunnerBase extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IIBTeststepRunnerBase.class);
    private static final int ACTIVITY_LOG_POLLING_TIMEOUT = 30;    // in seconds
    private BrokerConnectionParameters bcp;

    protected void setBrokerConnectionParameters(BrokerConnectionParameters bcp) {
        this.bcp = bcp;
    }

    public BasicTeststepRun run(Teststep teststep) throws Exception {
        String action = teststep.getAction();
        if (action == null) {
            throw new Exception("Action not specified.");
        }

        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();

        IIBTeststepProperties teststepProperties = (IIBTeststepProperties) teststep.getOtherProperties();
        BrokerProxy brokerProxy = null;
        try {
            //  connect to the broker
            brokerProxy = BrokerProxy.getInstance(bcp);
            brokerProxy.setSynchronous(90 * 1000);    //  do everything synchronously

            //  get message flow proxy
            MessageFlowProxy messageFlowProxy = getMessageFlowProxy(brokerProxy,
                    teststepProperties.getIntegrationServerName(), teststepProperties.getApplicationName(),
                    teststepProperties.getMessageFlowName());

            //  do the specified action
            if (Teststep.ACTION_START.equals(action)) {
                start(messageFlowProxy, basicTeststepRun);
            } else if (Teststep.ACTION_STOP.equals(action)) {
                stop(messageFlowProxy, basicTeststepRun);
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

        return basicTeststepRun;
    }

    private MessageFlowProxy getMessageFlowProxy(BrokerProxy brokerProxy, String integrationServerName,
                                                 String applicationName, String messageFlowName) throws Exception {
        //  get integration server proxy
        String integrationNodeName = brokerProxy.getName();
        ExecutionGroupProxy integrationServerProxy = brokerProxy.getExecutionGroupByName(integrationServerName);
        if (integrationServerProxy == null) {
            throw new Exception("Integration server \"" + integrationServerName +
                    "\" not found on integration node \"" + integrationNodeName + "\".");
        } else if (!integrationServerProxy.isRunning()) {
            throw new Exception("Integration server \"" + integrationServerName + "\" not running.");
        }

        //  get message flow proxy
        MessageFlowProxy messageFlowProxy;
        if ("".equals(StringUtils.trimToEmpty(applicationName))) {    //  application name not specified, message flow is at integration server level
            messageFlowProxy = integrationServerProxy.getMessageFlowByName(messageFlowName);
            if (messageFlowProxy == null) {
                throw new Exception("Message flow \"" + messageFlowName +
                        "\" not found on integration server \"" + integrationServerName + "\".");
            }
        } else {                       //  application name specified, message flow is at application level
            ApplicationProxy applicationProxy = integrationServerProxy.getApplicationByName(applicationName);
            if (applicationProxy == null) {
                throw new Exception("Application \"" + applicationName +
                        "\" not found on integration server \"" + integrationServerName + "\".");
            } else if (!applicationProxy.isRunning()) {
                throw new Exception("Application \"" + applicationName + "\" not running.");
            } else {
                messageFlowProxy = applicationProxy.getMessageFlowByName(messageFlowName);
                if (messageFlowProxy == null) {
                    throw new Exception("Message flow \"" + messageFlowName +
                            "\" not found in application \"" + applicationName +
                            "\" on integration server \"" + integrationServerName + "\".");
                }
            }
        }
        return messageFlowProxy;
    }

    private void start(MessageFlowProxy messageFlowProxy, BasicTeststepRun basicTeststepRun) throws Exception {
        if (messageFlowProxy.isRunning()) {
            basicTeststepRun.setInfoMessage("Message flow is already running");
        } else {
            messageFlowProxy.start();
            basicTeststepRun.setInfoMessage("Message flow started");
        }
    }

    private void stop(MessageFlowProxy messageFlowProxy, BasicTeststepRun basicTeststepRun) throws Exception {
        if (messageFlowProxy.isRunning()) {
            messageFlowProxy.stop();
            basicTeststepRun.setInfoMessage("Message flow stopped");
        } else {
            basicTeststepRun.setInfoMessage("Message flow is already stopped");
        }
    }

    private void waitForProcessingCompletion(MessageFlowProxy messageFlowProxy)
            throws Exception {
        if (!messageFlowProxy.isRunning()) {
            throw new Exception("Message flow not running.");
        } else {
            Date pollingEndTime = DateUtils.addSeconds(new Date(), ACTIVITY_LOG_POLLING_TIMEOUT);
            ActivityLogEntry processingCompletionSignal = null;
            while (System.currentTimeMillis() < pollingEndTime.getTime()) {
                ActivityLogProxy activityLogProxy = messageFlowProxy.getActivityLog();
                if (activityLogProxy != null) {
                    for (int i = 1; i <= activityLogProxy.getSize(); i++) {
                        ActivityLogEntry logEntry = activityLogProxy.getLogEntry(i);
                        if ((11506 == logEntry.getMessageNumber() || 11504 == logEntry.getMessageNumber()) &&
                                logEntry.getTimestamp().after(getTestcaseRunContext().getTestcaseRunStartTime())) {
                            processingCompletionSignal = logEntry;
                            break;
                        }
                    }
                }
                if (processingCompletionSignal != null) {
                    break;
                }
            }
            if (processingCompletionSignal == null) {
                throw new Exception("Message flow activity log polling timeout. No processing completion signal found.");
            } else {
                LOGGER.info("Message flow processing completion signal found. " + processingCompletionSignal.toString());
            }
        }
    }
}
