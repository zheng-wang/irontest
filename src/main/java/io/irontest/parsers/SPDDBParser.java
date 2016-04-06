package io.irontest.parsers;

import io.irontest.models.Properties;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class SPDDBParser implements IronTestParser {
    public String getSampleRequest(Properties details) {
        return "select * from ? where ?";
    }

    public String getAdhocAddress(Properties details) {
        return null;
    }

    public List<String> getProperties() {
        String[] properties = {};
        return Arrays.asList(properties);
    }
}
