package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class JMSTeststepProperties extends Properties {
    private JMSDestinationType destinationType = JMSDestinationType.QUEUE;    //  queue is the default destination type
    private String queueName;
    private String topicString;
    private Integer browseMessageIndex = 1;    //  only used in Browse action; default value 1

    public JMSDestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(JMSDestinationType destinationType) {
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

    public Integer getBrowseMessageIndex() {
        return browseMessageIndex;
    }

    public void setBrowseMessageIndex(Integer browseMessageIndex) {
        this.browseMessageIndex = browseMessageIndex;
    }
}