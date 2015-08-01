package au.com.billon.stt.parsers;

import au.com.billon.stt.models.Properties;

import java.util.List;

/**
 * Created by Trevor Li on 7/25/15.
 */
public interface STTParser {
    public String getSampleRequest(Properties details);
    public String getAdhocAddress(Properties details);
    public List<String> getProperties();
}
