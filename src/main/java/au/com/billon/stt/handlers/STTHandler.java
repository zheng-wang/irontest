package au.com.billon.stt.handlers;

import au.com.billon.stt.models.Endpoint;

import java.util.List;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface STTHandler {
    public String invoke(String request, Endpoint endpoint) throws Exception;
    public List<String> getProperties();
}
