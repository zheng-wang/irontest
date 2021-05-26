package io.irontest.core.teststep;

import com.solacesystems.jcsmp.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.JMSSolaceEndpointProperties;
import io.irontest.models.teststep.*;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

public class JMSSolaceTeststepRunner extends TeststepRunner {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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
            response = doQueueAction(teststepProperties, endpoint, action, teststep.getApiRequest());
        } else if (JMSDestinationType.TOPIC == teststepProperties.getDestinationType()) {
            doTopicAction(endpoint, teststepProperties.getTopicString(), teststep.getApiRequest());
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
        connectionFactory.setPassword(getDecryptedEndpointPassword());
        connectionFactory.setBrowserTimeoutInMS(200);    //  only needed for using JMS QueueBrowser
        return connectionFactory.createConnection();
    }

    private APIResponse doQueueAction(JMSTeststepProperties teststepOtherProperties, Endpoint endpoint, String action,
                                      APIRequest apiRequest) throws Exception {
        APIResponse response = null;

        switch (action) {
            case Teststep.ACTION_CLEAR:
                response = clearQueue(endpoint, teststepOtherProperties.getQueueName());
                break;
            case Teststep.ACTION_CHECK_DEPTH:
                response = checkDepth(endpoint, teststepOtherProperties.getQueueName());
                break;
            case Teststep.ACTION_SEND:
                sendMessageToQueue(endpoint, teststepOtherProperties.getQueueName(), apiRequest);
                break;
            case Teststep.ACTION_BROWSE:
                response = browseQueue(endpoint, teststepOtherProperties.getQueueName(),
                        teststepOtherProperties.getBrowseMessageIndex());
                break;
            default:
                throw new IllegalArgumentException("Unrecognized action " + action + ".");
        }

        return response;
    }

    private void doTopicAction(Endpoint endpoint, String topicString, APIRequest apiRequest) throws Exception {
        Connection connection = createJMSConnection(endpoint);
        javax.jms.Session session = null;

        try {
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            javax.jms.Topic topic = session.createTopic(topicString);
            MessageProducer messageProducer = session.createProducer(topic);
            JMSRequest request = (JMSRequest) apiRequest;
            javax.jms.TextMessage message = session.createTextMessage(request.getBody());
            for (JMSMessageProperty property: request.getProperties()) {
                message.setStringProperty(property.getName(), property.getValue());
            }

            messageProducer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
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
            properties.setWaitTimeout(200);                   //  50 is too small and often causes incomplete depth check
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

            messageProducer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private APIResponse browseQueue(Endpoint endpoint, String queueName, int browseMessageIndex) throws Exception {
        JMSBrowseQueueResponse response = null;
        Connection connection = createJMSConnection(endpoint);
        javax.jms.Session session = null;

        try {
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            javax.jms.Queue queue = session.createQueue(queueName);

            QueueBrowser browser = session.createBrowser(queue);
            Enumeration<javax.jms.Message> messages = browser.getEnumeration();

            int index = 0;
            while (messages.hasMoreElements()) {
                index++;
                javax.jms.Message message = messages.nextElement();
                if (index == browseMessageIndex) {
                    response = new JMSBrowseQueueResponse();

                    //  set header
                    Map<String, String> header = response.getHeader();
                    header.put(JMSConstants.JMS_MESSAGE_ID, message.getJMSMessageID());
                    header.put(JMSConstants.JMS_CORRELATION_ID, message.getJMSCorrelationID());
                    header.put(JMSConstants.JMS_TIMESTAMP,
                            dateFormat.format(new Date(message.getJMSTimestamp())));
                    header.put(JMSConstants.JMS_TYPE, message.getJMSType());
                    header.put(JMSConstants.JMS_DESTINATION, message.getJMSDestination().toString());
                    String jmsDeliveryMode = JMSConstants.UNKNOWN;
                    if (message.getJMSDeliveryMode() == javax.jms.DeliveryMode.PERSISTENT) {
                        jmsDeliveryMode = JMSConstants.PERSISTENT;
                    } else if (message.getJMSDeliveryMode() == javax.jms.DeliveryMode.NON_PERSISTENT) {
                        jmsDeliveryMode = JMSConstants.NON_PERSISTENT;
                    }
                    header.put(JMSConstants.JMS_DELIVERY_MODE, jmsDeliveryMode);
                    header.put(JMSConstants.JMS_EXPIRATION, message.getJMSExpiration() == 0 ?
                            null : dateFormat.format(new Date(message.getJMSExpiration())));
                    header.put(JMSConstants.JMS_PRIORITY, Integer.toString(message.getJMSPriority()));
                    header.put(JMSConstants.JMS_REDELIVERED, Boolean.toString(message.getJMSRedelivered()));
                    header.put(JMSConstants.JMS_REPLY_TO, message.getJMSReplyTo() == null ?
                            null : message.getJMSReplyTo().toString());

                    //  set properties
                    Enumeration propertyNames = message.getPropertyNames();
                    while (propertyNames.hasMoreElements()) {
                        String propertyName = (String) propertyNames.nextElement();
                        response.getProperties().put(propertyName, message.getStringProperty(propertyName));
                    }

                    //  set body
                    String body;
                    if (message instanceof javax.jms.TextMessage) {
                        body = ((javax.jms.TextMessage) message).getText();
                    } else if (message instanceof javax.jms.BytesMessage) {
                        javax.jms.BytesMessage bytesMessage = (javax.jms.BytesMessage) message;
                        byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
                        bytesMessage.readBytes(bytes);
                        body = new String(bytes);
                    } else {
                        throw new RuntimeException("Message type " + message.getClass() + " currently unsupported.");
                    }
                    response.setBody(body);

                    break;
                }
            }
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return response;
    }
}