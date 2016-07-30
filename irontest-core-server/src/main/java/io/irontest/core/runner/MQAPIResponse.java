package io.irontest.core.runner;

/**
 * Wrap primitive value so that the JSON returned to browser can be parsed by angular resource.
 * Refer to https://github.com/angular/angular.js/issues/12787).
 * Created by Zheng on 23/07/2016.
 */
public class MQAPIResponse {
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String toString() {
        return "value: " + value;
    }
}
