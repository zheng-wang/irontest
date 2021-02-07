package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JMSDestinationType {
    QUEUE("Queue"), TOPIC("Topic");

    private final String text;

    JMSDestinationType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static JMSDestinationType getByText(String text) {
        for (JMSDestinationType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
