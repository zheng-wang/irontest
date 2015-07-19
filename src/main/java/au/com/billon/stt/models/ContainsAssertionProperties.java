package au.com.billon.stt.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Zheng on 19/07/2015.
 */
public class ContainsAssertionProperties extends Properties {
    private String contains;

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }
}
