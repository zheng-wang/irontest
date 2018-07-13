package io.irontest.core;

import org.apache.commons.text.StrLookup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapValueLookup extends StrLookup<String> {
    private Map<String, String> map;
    private boolean escapeForJSON;
    private Set<String> unfoundKeys = new HashSet<>();

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
                if (escapeForJSON) {  //  replace \ and " with \\ and \" for the result to be used in JSON string
                    value = value.replace("\\", "\\\\");
                    value = value.replace("\"", "\\\"");
                }
                return value;
            }
        }
        unfoundKeys.add(key);
        return null;    //  returning null leaves the reference untouched (not replaced) in the template string
    }

    public Set<String> getUnfoundKeys() {
        return unfoundKeys;
    }
}
