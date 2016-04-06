package io.irontest.parsers;

import io.irontest.models.Properties;

import java.util.List;

/**
 * Created by Trevor Li on 7/25/15.
 */
public interface IronTestParser {
    public String getSampleRequest(Properties details);
    public String getAdhocAddress(Properties details);
    public List<String> getProperties();
}
