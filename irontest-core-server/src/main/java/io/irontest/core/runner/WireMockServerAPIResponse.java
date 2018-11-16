package io.irontest.core.runner;

/**
 * Use this class because it seems not simple for Freemarker to serialize Java object to JSON string.
 */
public class WireMockServerAPIResponse {
    private String allServeEvents;

    public String getAllServeEvents() {
        return allServeEvents;
    }

    public void setAllServeEvents(String allServeEvents) {
        this.allServeEvents = allServeEvents;
    }
}
