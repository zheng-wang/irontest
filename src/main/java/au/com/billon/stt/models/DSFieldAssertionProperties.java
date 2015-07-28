package au.com.billon.stt.models;

/**
 * Created by Zheng on 19/07/2015.
 */
public class DSFieldAssertionProperties extends AssertionProperties {
    private String field;
    private String operator;
    private String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
