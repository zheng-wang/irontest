package io.irontest.models.assertion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.JsonViews;
import io.irontest.models.Properties;

/**
 * Created by Zheng on 19/07/2015.
 */
public class Assertion {
    public static final String TYPE_CONTAINS = "Contains";
    public static final String TYPE_XPATH = "XPath";
    public static final String TYPE_DSFIELD = "DSField";
    public static final String TYPE_INTEGER_EQUAL = "IntegerEqual";
    public static final String TYPE_XML_EQUAL = "XMLEqual";
    private Long id;
    private String name;
    @JsonView(JsonViews.TestcaseRun.class)
    private String type;
    @JsonView(JsonViews.TestcaseRun.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ContainsAssertionProperties.class, name = Assertion.TYPE_CONTAINS),
            @JsonSubTypes.Type(value = XPathAssertionProperties.class, name = Assertion.TYPE_XPATH),
            @JsonSubTypes.Type(value = DSFieldAssertionProperties.class, name = Assertion.TYPE_DSFIELD),
            @JsonSubTypes.Type(value = IntegerEqualAssertionProperties.class,
                    name = Assertion.TYPE_INTEGER_EQUAL),
            @JsonSubTypes.Type(value = XMLEqualAssertionProperties.class, name = Assertion.TYPE_XML_EQUAL)})
    private Properties otherProperties;

    public Assertion() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Properties otherProperties) {
        this.otherProperties = otherProperties;
    }
}
