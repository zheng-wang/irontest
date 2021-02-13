package io.irontest.core.teststep;

import com.solacesystems.jcsmp.*;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.JMSSolaceEndpointProperties;
import io.irontest.models.teststep.JMSDestinationType;
import io.irontest.models.teststep.JMSTeststepProperties;
import io.irontest.models.teststep.Teststep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSSolaceTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(JMSSolaceTeststepRunner.class);

    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        String action = teststep.getAction();
        if (teststep.getAction() == null) {
            throw new Exception("Action not specified.");
        }
        JMSTeststepProperties teststepProperties = (JMSTeststepProperties) teststep.getOtherProperties();
        if (teststepProperties.getDestinationType() == null) {
            throw new Exception("Destination type not specified.");
        }

        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();

        APIResponse response = null;
        Endpoint endpoint = teststep.getEndpoint();
        JMSSolaceEndpointProperties endpointProperties = (JMSSolaceEndpointProperties) endpoint.getOtherProperties();
        JCSMPSession session = null;
        try {
            //  create session
            JCSMPProperties properties = new JCSMPProperties();
            properties.setProperty(JCSMPProperties.HOST, endpoint.getHost() + ":" + endpoint.getPort());
            properties.setProperty(JCSMPProperties.USERNAME, endpoint.getUsername());
            properties.setProperty(JCSMPProperties.PASSWORD, getDecryptedEndpointPassword());
            properties.setProperty(JCSMPProperties.VPN_NAME,  endpointProperties.getVpn());
            session = JCSMPFactory.onlyInstance().createSession(properties);

            if (JMSDestinationType.QUEUE == teststepProperties.getDestinationType()) {
                response = doQueueAction(session, teststepProperties.getQueueName(), action);
            } else if (JMSDestinationType.TOPIC == teststepProperties.getDestinationType()) {
            }
        } finally {
            if (session != null) {
                session.closeSession();
            }
        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }

    private APIResponse doQueueAction(JCSMPSession session, String queueName, String action) throws JCSMPException {
        APIResponse response = null;
        Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        //  do the action
        switch (action) {
            case Teststep.ACTION_CLEAR:
                break;
            case Teststep.ACTION_CHECK_DEPTH:
                response = checkDepth(session, queue);
                break;
            case Teststep.ACTION_SEND:
                break;
            case Teststep.ACTION_BROWSE:
                break;
            default:
                throw new IllegalArgumentException("Unrecognized action " + action + ".");
        }

        return response;
    }

    private MQCheckQueueDepthResponse checkDepth(JCSMPSession session, Queue queue) throws JCSMPException {
        MQCheckQueueDepthResponse response = new MQCheckQueueDepthResponse();

        BrowserProperties properties = new BrowserProperties();
        properties.setEndpoint(queue);
        properties.setWaitTimeout(50);
        Browser browser = session.createBrowser(properties);
        BytesXMLMessage message;
        int queueDepth = 0;
        do {
            message = browser.getNext();
            if (message != null) {
                queueDepth++;
            }
        } while (message != null);

        response.setQueueDepth(queueDepth);

        return response;
    }
}