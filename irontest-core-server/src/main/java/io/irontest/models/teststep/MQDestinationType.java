package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Zheng on 13/03/2017.
 */
public enum MQDestinationType {
    QUEUE("Queue"), TOPIC("Topic");

    private final String text;

    MQDestinationType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static MQDestinationType getByText(String text) {
        for (MQDestinationType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
