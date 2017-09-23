package io.irontest.core;

import org.apache.commons.text.StrLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 10/09/2017.
 */
public class MapValueLookup extends StrLookup<String> {
    private Map<String, String> map;
    private boolean escapeForJSON;
    private List<String> unfoundKeys = new ArrayList<String>();

    public MapValueLookup(Map<String, String> map, boolean escapeForJSON) {
        this.map = map;
        this.escapeForJSON = escapeForJSON;
    }

    @Override
    public String lookup(String key) {
        key = key.trim();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            if (key.equals(entry.getKey())) {
                String value = entry.getValue();
                if (escapeForJSON) {
                    value = value.replace("\"", "\\\"");   //  replace " with \" for the result to be used in JSON string
                }
                return value;
            }
        }
        unfoundKeys.add(key);
        return null;    //  returning null leaves the reference untouched (not replaced) in the template string
    }

    public List<String> getUnfoundKeys() {
        return unfoundKeys;
    }
}
