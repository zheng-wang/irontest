package au.com.billon.stt.handlers;

import au.com.billon.stt.models.Endpoint;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface STTHandler {
    public Object invoke(String request, Map<String, String> details) throws Exception;
    public List<String> getProperties();
}
