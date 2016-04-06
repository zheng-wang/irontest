package io.irontest.handlers;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface IronTestHandler {
    public Object invoke(String request, Map<String, String> details) throws Exception;
    public List<String> getProperties();
}
