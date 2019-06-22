package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class MQTeststepProperties extends Properties {
    private MQDestinationType destinationType = MQDestinationType.QUEUE;       //  queue is the default destination type
    private String queueName;
    private String topicString;
    private MQRFH2Header rfh2Header;  // for Enqueue action and Publish action with message from text; null means no RFH2 header

    public MQDestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(MQDestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getTopicString() {
        return topicString;
    }

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }

    public MQRFH2Header getRfh2Header() {
        return rfh2Header;
    }

    public void setRfh2Header(MQRFH2Header rfh2Header) {
        this.rfh2Header = rfh2Header;
    }
}