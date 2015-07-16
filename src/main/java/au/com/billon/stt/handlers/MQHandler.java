package au.com.billon.stt.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class MQHandler implements STTHandler {
    public MQHandler() { }

    public String invoke(String request, Map<String, String> details) throws Exception {
        return null;
    }

    public List<String> getProperties() {
        String[] properties = {"host", "port", "channel", "manager", "queue", "userid", "password"};
        return Arrays.asList(properties);
    }
}
