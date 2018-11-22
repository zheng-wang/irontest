package io.irontest.core.runner;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import java.util.List;

public class WireMockServerAPIResponse {
    private boolean serializeFieldValueToJSONInFreeMarkerTemplate = true;
    private List<ServeEvent> allServeEvents;

    public List<ServeEvent> getAllServeEvents() {
        return allServeEvents;
    }

    public void setAllServeEvents(List<ServeEvent> allServeEvents) {
        this.allServeEvents = allServeEvents;
    }

    public boolean isSerializeFieldValueToJSONInFreeMarkerTemplate() {
        return serializeFieldValueToJSONInFreeMarkerTemplate;
    }
}
