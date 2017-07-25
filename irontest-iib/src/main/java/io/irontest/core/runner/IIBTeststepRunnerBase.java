package io.irontest.core.runner;

import com.ibm.broker.config.proxy.*;
import io.irontest.models.teststep.IIBTeststepProperties;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Zheng on 25/07/2017.
 */
public class IIBTeststepRunnerBase extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(IIBTeststepRunnerBase.class);
    private static final int ACTIVITY_LOG_POLLING_TIMEOUT = 60;    // in seconds
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
                        if (11504 == logEntry.getMessageNumber() &&
                                logEntry.getTimestamp().after(getTestcaseRunContext().getTestcaseRunStartTime())) {
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
}
