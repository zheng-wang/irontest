package io.irontest.models.teststep;

public interface JMSConstants {
    //  header field names
    String JMS_MESSAGE_ID = "JMSMessageID";
    String JMS_CORRELATION_ID = "JMSCorrelationID";
    String JMS_TIMESTAMP = "JMSTimestamp";
    String JMS_TYPE = "JMSType";
    String JMS_DESTINATION = "JMSDestination";
    String JMS_DELIVERY_MODE = "JMSDeliveryMode";
    String JMS_EXPIRATION = "JMSExpiration";
    String JMS_PRIORITY = "JMSPriority";
    String JMS_REDELIVERED = "JMSRedelivered";
    String JMS_REPLY_TO = "JMSReplyTo";

    //  delivery modes
    String UNKNOWN = "UNKNOWN";
    String PERSISTENT = "PERSISTENT";
    String NON_PERSISTENT = "NON_PERSISTENT";
}
