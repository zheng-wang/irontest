package au.com.billon.stt.parsers;

import au.com.billon.stt.models.TeststepProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public interface STTParser {
    public String getSampleRequest(TeststepProperties details);
    public String getAdhocAddress(TeststepProperties details);
    public List<String> getProperties();
}
