package io.irontest.core.teststep;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

import java.util.ArrayList;
import java.util.List;

public class WireMockServerAPIResponse extends APIResponse {
    private List<ServeEvent> allServeEvents = new ArrayList<>();

    public List<ServeEvent> getAllServeEvents() {
        return allServeEvents;
    }

    public void setAllServeEvents(List<ServeEvent> allServeEvents) {
        this.allServeEvents = allServeEvents;
    }
}
