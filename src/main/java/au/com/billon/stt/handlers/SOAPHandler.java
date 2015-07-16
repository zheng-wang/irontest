package au.com.billon.stt.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPHandler implements STTHandler {
    public SOAPHandler() { }

    public String invoke(String request, Map<String, String> details) throws Exception {
        return null;
    }

    public List<String> getProperties() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
