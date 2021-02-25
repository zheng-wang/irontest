package io.irontest.core.teststep;

import com.solacesystems.jcsmp.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.JMSSolaceEndpointProperties;
import io.irontest.models.teststep.*;

import javax.jms.Connection;
import javax.jms.MessageProducer;

public class JMSSolaceTeststepRunner extends TeststepRunner {
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
        if (JMSDestinationType.QUEUE == teststepProperties.getDestinationType()) {
            response = doQueueAction(endpoint, teststepProperties.getQueueName(), action, teststep.getApiRequest());
        } else if (JMSDestinationType.TOPIC == teststepProperties.getDestinationType()) {
        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }

    private JCSMPSession createJCSMPSession(Endpoint endpoint) throws InvalidPropertiesException {
        JMSSolaceEndpointProperties endpointProperties = (JMSSolaceEndpointProperties) endpoint.getOtherProperties();
        JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, endpoint.getHost() + ":" + endpoint.getPort());
        properties.setProperty(JCSMPProperties.USERNAME, endpoint.getUsername());
        properties.setProperty(JCSMPProperties.PASSWORD, getDecryptedEndpointPassword());
        properties.setProperty(JCSMPProperties.VPN_NAME,  endpointProperties.getVpn());
        return JCSMPFactory.onlyInstance().createSession(properties);
    }

    private Connection createJMSConnection(Endpoint endpoint) throws Exception {
        JMSSolaceEndpointProperties endpointProperties = (JMSSolaceEndpointProperties) endpoint.getOtherProperties();
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(endpoint.getHost());
        connectionFactory.setPort(endpoint.getPort());
        connectionFactory.setVPN(endpointProperties.getVpn());
        connectionFactory.setUsername(endpoint.getUsername());
        connectionFactory.setPassword(endpoint.getPassword());
        return connectionFactory.createConnection();
    }

    private APIResponse doQueueAction(Endpoint endpoint, String queueName, String action, APIRequest apiRequest) throws Exception {
        APIResponse response = null;

        //  do the action
        switch (action) {
            case Teststep.ACTION_CLEAR:
                response = clearQueue(endpoint, queueName);
                break;
            case Teststep.ACTION_CHECK_DEPTH:
                response = checkDepth(endpoint, queueName);
                break;
            case Teststep.ACTION_SEND:
                sendMessageToQueue(endpoint, queueName, apiRequest);
                break;
            case Teststep.ACTION_BROWSE:
                break;
            default:
                throw new IllegalArgumentException("Unrecognized action " + action + ".");
        }

        return response;
    }

    private JMSClearQueueResponse clearQueue(Endpoint endpoint, String queueName) throws JCSMPException {
        JMSClearQueueResponse response = new JMSClearQueueResponse();
        int clearedMessagesCount = 0;
        JCSMPSession session = createJCSMPSession(endpoint);
        try {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

            ConsumerFlowProperties consumerFlowProperties = new ConsumerFlowProperties();
            consumerFlowProperties.setEndpoint(queue);

            FlowReceiver receiver = session.createFlow(null, consumerFlowProperties, null);
            receiver.start();
            BytesXMLMessage msg;

            do {
                msg = receiver.receive(100);      //  this timeout is only affecting the time wait after fetching the last message, or when the queue is empty
                if (msg != null) {
                    clearedMessagesCount++;
                }
            } while (msg != null);

            receiver.close();
        } finally {
            if (session != null) {
                session.closeSession();
            }
        }

        response.setClearedMessagesCount(clearedMessagesCount);

        return response;
    }

    private JMSCheckQueueDepthResponse checkDepth(Endpoint endpoint, String queueName) throws JCSMPException {
        JMSCheckQueueDepthResponse response = new JMSCheckQueueDepthResponse();
        int queueDepth = 0;
        JCSMPSession session = createJCSMPSession(endpoint);
        try {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

            BrowserProperties properties = new BrowserProperties();
            properties.setEndpoint(queue);
            properties.setWaitTimeout(50);
            Browser browser = session.createBrowser(properties);
            BytesXMLMessage message;
            do {
                message = browser.getNext();
                if (message != null) {
                    queueDepth++;
                }
            } while (message != null);
        } finally {
            if (session != null) {
                session.closeSession();
            }
        }

        response.setQueueDepth(queueDepth);

        return response;
    }

    private void sendMessageToQueue(Endpoint endpoint, String queueName, APIRequest apiRequest) throws Exception {
        Connection connection = createJMSConnection(endpoint);
        javax.jms.Session session = null;

        try {
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            javax.jms.Queue queue = session.createQueue(queueName);
            MessageProducer messageProducer = session.createProducer(queue);
            JMSRequest request = (JMSRequest) apiRequest;
            javax.jms.TextMessage message = session.createTextMessage(request.getBody());
            for (JMSMessageProperty property: request.getProperties()) {
                message.setStringProperty(property.getName(), property.getValue());
            }

            messageProducer.send(queue, message);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}