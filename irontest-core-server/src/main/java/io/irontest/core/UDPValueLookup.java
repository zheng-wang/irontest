package io.irontest.core;

import io.irontest.models.UserDefinedProperty;
import org.apache.commons.text.StrLookup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zheng on 10/09/2017.
 */
public class UDPValueLookup extends StrLookup<String> {
    private List<UserDefinedProperty> udps;
    private boolean escapeForJSON;
    private List<String> undefinedProperties = new ArrayList<String>();

    public UDPValueLookup(List<UserDefinedProperty> udps, boolean escapeForJSON) {
        this.udps = udps;
        this.escapeForJSON = escapeForJSON;
    }

    @Override
    public java.lang.String lookup(java.lang.String key) {
        key = key.trim();
        for (UserDefinedProperty udp: udps) {
            if (key.equals(udp.getName())) {
                String value = udp.getValue();
                if (escapeForJSON) {
                    value = value.replace("\"", "\\\"");   //  replace " with \" for the result to be used in JSON string
                }
                return value;
            }
        }
        undefinedProperties.add(key);
        return null;    //  returning null preserves the property reference untouched (not replaced) in the template string
    }

    public List<String> getUndefinedProperties() {
        return undefinedProperties;
    }
}
